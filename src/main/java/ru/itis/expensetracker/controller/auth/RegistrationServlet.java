package ru.itis.expensetracker.controller.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(RegistrationServlet.class);
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
        String email = null;
        try {
            String username = req.getParameter("username");
            email = req.getParameter("email");
            String password = req.getParameter("password");
            String confirmPassword = req.getParameter("confirmPassword");

            // Базовая проверка на null (детальная валидация в сервисе)
            if (username == null || email == null || password == null || confirmPassword == null) {
                req.setAttribute("error", "Все поля обязательны для заполнения.");
                req.getRequestDispatcher("/WEB-INF/jsp/auth/registration.jsp").forward(req, resp);
                return;
            }

            String emailTrimmed = email.trim();
            authService.register(username.trim(), emailTrimmed, password, confirmPassword);
            logger.info("User registration successful: {}", emailTrimmed);
            resp.sendRedirect(req.getContextPath() + "/login");
        } catch (ServiceException e) {
            String emailValue = email != null ? email : "unknown";
            logger.warn("Registration failed for email {}: {}", emailValue, e.getMessage());
            req.setAttribute("error", e.getMessage());
            req.getRequestDispatcher("/WEB-INF/jsp/auth/registration.jsp").forward(req, resp);
        } catch (Exception e) {
            logger.error("Unexpected error during registration", e);
            req.setAttribute("error", "Произошла ошибка при регистрации. Попробуйте позже.");
            req.getRequestDispatcher("/WEB-INF/jsp/auth/registration.jsp").forward(req, resp);
        }
    }
}
