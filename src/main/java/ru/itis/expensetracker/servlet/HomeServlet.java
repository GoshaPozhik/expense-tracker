package ru.itis.expensetracker.servlet;

import java.io.IOException;
import java.util.List;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.itis.expensetracker.model.User;
import ru.itis.expensetracker.model.Wallet;
import ru.itis.expensetracker.service.WalletService;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(HomeServlet.class);
    private WalletService walletService;

    @Override
    public void init() {
        walletService = (WalletService) getServletContext().getAttribute("walletService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            User user = (User) req.getSession().getAttribute("user");
            if (user == null) {
                logger.warn("Unauthorized access attempt to /home");
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Пользователь не авторизован.");
                return;
            }

            List<Wallet> wallets = walletService.getWalletsForUser(user.getId());
            logger.debug("User {} loaded {} wallets", user.getId(), wallets.size());

            req.setAttribute("wallets", wallets);
            req.getRequestDispatcher("/WEB-INF/jsp/wallets/list.jsp").forward(req, resp);
        } catch (Exception e) {
            logger.error("Error loading wallets for user", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Произошла ошибка при загрузке кошельков.");
        }
    }
}