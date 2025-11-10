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
        try {
            String username = req.getParameter("username");
            String email = req.getParameter("email");
            String password = req.getParameter("password");
            String confirmPassword = req.getParameter("confirmPassword");

            if (username == null || username.trim().isEmpty()) {
                req.setAttribute("error", "Имя пользователя не может быть пустым.");
                req.getRequestDispatcher("/WEB-INF/jsp/auth/registration.jsp").forward(req, resp);
                return;
            }

            if (email == null || email.trim().isEmpty()) {
                req.setAttribute("error", "Email не может быть пустым.");
                req.getRequestDispatcher("/WEB-INF/jsp/auth/registration.jsp").forward(req, resp);
                return;
            }

            if (password == null || password.trim().isEmpty()) {
                req.setAttribute("error", "Пароль не может быть пустым.");
                req.getRequestDispatcher("/WEB-INF/jsp/auth/registration.jsp").forward(req, resp);
                return;
            }

            if (confirmPassword == null || confirmPassword.trim().isEmpty()) {
                req.setAttribute("error", "Подтверждение пароля не может быть пустым.");
                req.getRequestDispatcher("/WEB-INF/jsp/auth/registration.jsp").forward(req, resp);
                return;
            }

            authService.register(username.trim(), email.trim(), password, confirmPassword);
            resp.sendRedirect(req.getContextPath() + "/login");
        } catch (ServiceException e) {
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/auth/registration.jsp").forward(req, resp);
        } catch (Exception e) {
            req.setAttribute("error", "Произошла ошибка при регистрации. Попробуйте позже.");
            req.getRequestDispatcher("/WEB-INF/jsp/auth/registration.jsp").forward(req, resp);
        }
    }
}
