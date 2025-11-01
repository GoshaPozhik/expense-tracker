package ru.itis.expensetracker.controller.auth;

import ru.itis.expensetracker.exception.ServiceException;
import ru.itis.expensetracker.service.AuthService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {
    private AuthService authService;

    @Override
    public void init() {
        authService = (AuthService) getServletContext().getAttribute("authService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/auth/registration.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        try {
            authService.register(username, email, password);
            // После успешной регистрации перенаправляем на страницу входа
            resp.sendRedirect(req.getContextPath() + "/login");
        } catch (ServiceException e) {
            // Если возникла ошибка (например, email занят), возвращаем пользователя на страницу регистрации с сообщением
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/auth/registration.jsp").forward(req, resp);
        }
    }
}