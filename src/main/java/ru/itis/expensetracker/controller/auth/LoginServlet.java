package ru.itis.expensetracker.controller.auth;

import ru.itis.expensetracker.model.User;
import ru.itis.expensetracker.service.AuthService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Optional;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private AuthService authService;

    @Override
    public void init() {
        authService = (AuthService) getServletContext().getAttribute("authService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String email = req.getParameter("email");
            String password = req.getParameter("password");

            if (email == null || email.trim().isEmpty()) {
                req.setAttribute("error", "Email не может быть пустым.");
                req.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(req, resp);
                return;
            }

            if (password == null || password.trim().isEmpty()) {
                req.setAttribute("error", "Пароль не может быть пустым.");
                req.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(req, resp);
                return;
            }

            Optional<User> userOptional = authService.login(email.trim(), password);

            if (userOptional.isPresent()) {
                HttpSession session = req.getSession();
                session.setAttribute("user", userOptional.get());
                resp.sendRedirect(req.getContextPath() + "/home");
            } else {
                req.setAttribute("error", "Неверный email или пароль.");
                req.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(req, resp);
            }
        } catch (Exception e) {
            req.setAttribute("error", "Произошла ошибка при входе в систему.");
            req.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(req, resp);
        }
    }
}
