package ru.itis.expensetracker.servlet.expenses;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.itis.expensetracker.exception.ServiceException;
import ru.itis.expensetracker.model.User;
import ru.itis.expensetracker.service.WalletService;

@WebServlet("/expenses/delete")
public class DeleteExpenseServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(DeleteExpenseServlet.class);
    private WalletService walletService;

    @Override
    public void init() {
        walletService = (WalletService) getServletContext().getAttribute("walletService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String walletIdParam = req.getParameter("walletId");
            String expenseIdParam = req.getParameter("expenseId");
            User user = (User) req.getSession().getAttribute("user");

            if (user == null) {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Пользователь не авторизован.");
                return;
            }

            if (walletIdParam == null || walletIdParam.trim().isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID кошелька не указан.");
                return;
            }

            if (expenseIdParam == null || expenseIdParam.trim().isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID расхода не указан.");
                return;
            }

            long walletId = Long.parseLong(walletIdParam);
            long expenseId = Long.parseLong(expenseIdParam);
            String redirectUrl = req.getContextPath() + "/expenses?walletId=" + walletId;

            walletService.deleteExpense(expenseId, user.getId());
            logger.info("Expense deleted: expenseId={}, walletId={}, userId={}", expenseId, walletId, user.getId());
            resp.sendRedirect(redirectUrl);
        } catch (NumberFormatException e) {
            String walletIdParam = req.getParameter("walletId");
            if (walletIdParam != null && !walletIdParam.trim().isEmpty()) {
                try {
                    long walletId = Long.parseLong(walletIdParam);
                    String redirectUrl = req.getContextPath() + "/expenses?walletId=" + walletId;
                    String errorMsg = URLEncoder.encode("Некорректный ID для удаления.", StandardCharsets.UTF_8);
                    redirectUrl += "&error=" + errorMsg;
                    resp.sendRedirect(redirectUrl);
                } catch (NumberFormatException ex) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный ID кошелька.");
                }
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный ID для удаления.");
            }
        } catch (ServiceException e) {
            logger.warn("Error deleting expense: {}", e.getMessage());
            String walletIdParam = req.getParameter("walletId");
            if (walletIdParam != null && !walletIdParam.trim().isEmpty()) {
                try {
                    long walletId = Long.parseLong(walletIdParam);
                    String redirectUrl = req.getContextPath() + "/expenses?walletId=" + walletId;
                    String errorMsg = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
                    redirectUrl += "&error=" + errorMsg;
                    resp.sendRedirect(redirectUrl);
                } catch (NumberFormatException ex) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                }
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Unexpected error deleting expense", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Произошла ошибка при удалении расхода.");
        }
    }
}
