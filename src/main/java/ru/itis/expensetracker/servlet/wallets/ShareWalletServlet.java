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

@WebServlet("/wallets/share")
public class ShareWalletServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(ShareWalletServlet.class);
    private WalletService walletService;

    @Override
    public void init() {
        walletService = (WalletService) getServletContext().getAttribute("walletService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String emailToShare = req.getParameter("emailToShare");
            String walletIdParam = req.getParameter("walletId");
            User owner = (User) req.getSession().getAttribute("user");

            if (owner == null) {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Пользователь не авторизован.");
                return;
            }

            if (emailToShare == null || emailToShare.trim().isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Email не может быть пустым.");
                return;
            }

            if (walletIdParam == null || walletIdParam.trim().isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID кошелька не указан.");
                return;
            }

            long walletId = Long.parseLong(walletIdParam);
            String redirectUrl = req.getContextPath() + "/expenses?walletId=" + walletId;

            walletService.shareWallet(walletId, owner.getId(), emailToShare.trim());
            logger.info("Wallet shared: walletId={}, ownerId={}, sharedWith={}", walletId, owner.getId(), emailToShare);
            redirectUrl += "&share_success=true";
            resp.sendRedirect(redirectUrl);
        } catch (NumberFormatException e) {
            logger.warn("Invalid wallet ID format: {}", req.getParameter("walletId"));
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный ID кошелька.");
        } catch (ServiceException e) {
            logger.warn("Error sharing wallet: {}", e.getMessage());
            String walletIdParam = req.getParameter("walletId");
            if (walletIdParam != null && !walletIdParam.trim().isEmpty()) {
                try {
                    long walletId = Long.parseLong(walletIdParam);
                    String redirectUrl = req.getContextPath() + "/expenses?walletId=" + walletId;
                    redirectUrl += "&share_error=" + URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
                    resp.sendRedirect(redirectUrl);
                } catch (NumberFormatException ex) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                }
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Unexpected error sharing wallet", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Произошла ошибка при предоставлении доступа к кошельку.");
        }
    }
}
