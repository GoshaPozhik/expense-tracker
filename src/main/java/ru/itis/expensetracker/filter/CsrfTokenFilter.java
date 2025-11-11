package ru.itis.expensetracker.filter;

import ru.itis.expensetracker.util.CsrfTokenUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Фильтр, который добавляет CSRF-токен в атрибуты запроса для всех GET-запросов
 * Это позволяет использовать токен в JSP-формах
 */
@WebFilter("/*")
public class CsrfTokenFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // Для GET-запросов генерируем или получаем токен и добавляем в атрибуты
        if ("GET".equals(request.getMethod())) {
            HttpSession session = request.getSession(true);
            String token = CsrfTokenUtil.getToken(session);
            request.setAttribute("csrfToken", token);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}
}

