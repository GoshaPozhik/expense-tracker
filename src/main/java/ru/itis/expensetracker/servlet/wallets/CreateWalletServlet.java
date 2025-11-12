package ru.itis.expensetracker.servlet.wallets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itis.expensetracker.exception.ServiceException;
import ru.itis.expensetracker.model.User;
import ru.itis.expensetracker.service.WalletService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/wallets/create")
public class CreateWalletServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(CreateWalletServlet.class);
    private WalletService walletService;

    @Override
    public void init() {
        walletService = (WalletService) getServletContext().getAttribute("walletService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String walletName = req.getParameter("walletName");
            User user = (User) req.getSession().getAttribute("user");

            if (user == null) {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Пользователь не авторизован.");
                return;
            }

            walletService.createWallet(walletName, user.getId());
            logger.info("Wallet created: name={}, userId={}", walletName, user.getId());
            resp.sendRedirect(req.getContextPath() + "/home");
        } catch (ServiceException e) {
            logger.warn("Error creating wallet: {}", e.getMessage());
            req.setAttribute("error", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/home?error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.error("Unexpected error creating wallet", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Произошла ошибка при создании кошелька.");
        }
    }
}
