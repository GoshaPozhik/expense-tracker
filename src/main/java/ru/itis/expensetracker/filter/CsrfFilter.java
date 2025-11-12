package ru.itis.expensetracker.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itis.expensetracker.util.CsrfTokenUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class CsrfFilter implements Filter {
    private static final Logger logger = LoggerFactory.getLogger(CsrfFilter.class);

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String method = request.getMethod();
        String uri = request.getRequestURI();

        if ("GET".equals(method) || uri.endsWith(".css") || uri.endsWith(".js")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session;
        if (uri.endsWith("/login") || uri.endsWith("/register")) {
            session = request.getSession(true);
        } else {
            session = request.getSession(false);
            if (session == null) {
                chain.doFilter(request, response);
                return;
            }
        }

        String submittedToken = request.getParameter("csrfToken");
        
        if (!CsrfTokenUtil.isValidToken(session, submittedToken)) {
            logger.warn("CSRF token validation failed for URI: {}, IP: {}", uri, request.getRemoteAddr());
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Недействительный CSRF-токен. Возможна попытка повторной отправки формы.");
            return;
        }

        CsrfTokenUtil.generateToken(session);
        
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}
}

