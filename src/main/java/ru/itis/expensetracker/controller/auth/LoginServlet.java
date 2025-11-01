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
        String email = req.getParameter("email");
        String password = req.getParameter("password");

        Optional<User> userOptional = authService.login(email, password);

        if (userOptional.isPresent()) {
            // Сохраняем пользователя в сессию
            HttpSession session = req.getSession();
            session.setAttribute("user", userOptional.get());
            // Перенаправляем на главную страницу
            resp.sendRedirect(req.getContextPath() + "/home");
        } else {
            // Неверные данные, возвращаем на страницу входа с ошибкой
            req.setAttribute("error", "Неверный email или пароль.");
            req.getRequestDispatcher("/WEB-INF/jsp/auth/login.jsp").forward(req, resp);
        }
    }
}