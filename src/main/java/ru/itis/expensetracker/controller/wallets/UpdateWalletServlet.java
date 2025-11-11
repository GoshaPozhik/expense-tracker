package ru.itis.expensetracker.controller.wallets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itis.expensetracker.exception.ServiceException;
import ru.itis.expensetracker.model.User;
import ru.itis.expensetracker.model.Wallet;
import ru.itis.expensetracker.service.WalletService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/wallets/edit")
public class UpdateWalletServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(UpdateWalletServlet.class);
    private WalletService walletService;

    @Override
    public void init() {
        walletService = (WalletService) getServletContext().getAttribute("walletService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String idParam = req.getParameter("id");
            User user = (User) req.getSession().getAttribute("user");

            if (user == null) {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Пользователь не авторизован.");
                return;
            }

            if (idParam == null || idParam.trim().isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID кошелька не указан.");
                return;
            }

            long walletId = Long.parseLong(idParam);
            Wallet wallet = walletService.getWalletById(walletId, user.getId());

            req.setAttribute("wallet", wallet);
            req.getRequestDispatcher("/WEB-INF/jsp/wallets/edit.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный ID кошелька.");
        } catch (ServiceException e) {
            req.setAttribute("error", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/home?error=" + 
                    URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.error("Error loading wallet for edit", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Произошла ошибка при загрузке кошелька.");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String idParam = req.getParameter("walletId");
            String walletName = req.getParameter("walletName");
            User user = (User) req.getSession().getAttribute("user");

            if (user == null) {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Пользователь не авторизован.");
                return;
            }

            if (idParam == null || idParam.trim().isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID кошелька не указан.");
                return;
            }

            long walletId = Long.parseLong(idParam);
            walletService.updateWallet(walletId, walletName, user.getId());

            resp.sendRedirect(req.getContextPath() + "/home?success=" + 
                    URLEncoder.encode("Кошелек успешно обновлен.", StandardCharsets.UTF_8));
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный ID кошелька.");
        } catch (ServiceException e) {
            logger.warn("Error updating wallet: {}", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/home?error=" + 
                    URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8));
        } catch (Exception e) {
            logger.error("Unexpected error updating wallet", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Произошла ошибка при обновлении кошелька.");
        }
    }
}

