package ru.itis.expensetracker.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itis.expensetracker.repository.UserRepository;
import ru.itis.expensetracker.repository.impl.JdbcUserRepository;
import ru.itis.expensetracker.util.CookieUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);
    private UserRepository userRepository;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        userRepository = new JdbcUserRepository();
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = request.getRequestURI();

        if (uri.endsWith("/login") || uri.endsWith("/register") || uri.endsWith(".css") || uri.endsWith(".js")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("user") == null) {
            if (CookieUtil.hasRememberMeCookie(request)) {
                CookieUtil.getUserIdFromCookie(request).ifPresent(userId -> {
                    try {
                        userRepository.findById(userId).ifPresent(user -> {
                            HttpSession newSession = request.getSession(true);
                            newSession.setAttribute("user", user);
                            logger.info("Auto-login from cookie for user: {}", userId);
                        });
                    } catch (Exception e) {
                        logger.error("Error during auto-login from cookie", e);
                        CookieUtil.deleteRememberMeCookies(response);
                    }
                });
            }
        }

        session = request.getSession(false);
        if (session == null || session.getAttribute("user") == null) {
            logger.debug("Unauthorized access attempt to: {}", uri);
            response.sendRedirect(request.getContextPath() + "/login");
        } else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {}
}