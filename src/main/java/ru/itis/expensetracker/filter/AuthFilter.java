package ru.itis.expensetracker.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebFilter("/*")
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        String uri = request.getRequestURI();

        // Разрешаем доступ к страницам логина, регистрации и статическим ресурсам (css, js)
        if (uri.endsWith("/login") || uri.endsWith("/register") || uri.endsWith(".css") || uri.endsWith(".js")) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false); // false - не создавать новую сессию, если ее нет

        // Если сессии нет или в ней нет атрибута "user", перенаправляем на страницу входа
        if (session == null || session.getAttribute("user") == null) {
            response.sendRedirect(request.getContextPath() + "/login");
        } else {
            // Пользователь авторизован, пропускаем запрос дальше
            chain.doFilter(request, response);
        }
    }

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void destroy() {}
}