package ru.itis.expensetracker.servlet.auth;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.itis.expensetracker.model.User;
import ru.itis.expensetracker.util.CookieUtil;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(LogoutServlet.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            User user = (User) session.getAttribute("user");
            if (user != null) {
                logger.info("User logged out: {}", user.getEmail());
            }
            session.invalidate();
        }

        CookieUtil.deleteRememberMeCookies(resp);

        resp.sendRedirect(req.getContextPath() + "/login");
    }
}