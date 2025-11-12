package ru.itis.expensetracker.servlet.expenses;

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
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/expenses/add")
public class AddExpenseServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(AddExpenseServlet.class);
    private WalletService walletService;

    @Override
    public void init() {
        walletService = (WalletService) getServletContext().getAttribute("walletService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String amountParam = req.getParameter("amount");
            String description = req.getParameter("description");
            String categoryIdParam = req.getParameter("categoryId");
            String walletIdParam = req.getParameter("walletId");
            User user = (User) req.getSession().getAttribute("user");

            if (user == null) {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Пользователь не авторизован.");
                return;
            }

            if (amountParam == null || amountParam.trim().isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Сумма не может быть пустой.");
                return;
            }

            if (categoryIdParam == null || categoryIdParam.trim().isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Категория не выбрана.");
                return;
            }

            if (walletIdParam == null || walletIdParam.trim().isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Кошелек не выбран.");
                return;
            }

            BigDecimal amount = new BigDecimal(amountParam);
            long categoryId = Long.parseLong(categoryIdParam);
            long walletId = Long.parseLong(walletIdParam);

            walletService.addExpense(amount, description, user.getId(), walletId, categoryId);
            logger.info("Expense added: amount={}, walletId={}, userId={}", amount, walletId, user.getId());
            resp.sendRedirect(req.getContextPath() + "/expenses?walletId=" + walletId);
        } catch (NumberFormatException e) {
            String walletIdParam = req.getParameter("walletId");
            if (walletIdParam != null && !walletIdParam.trim().isEmpty()) {
                try {
                    long walletId = Long.parseLong(walletIdParam);
                    String errorMsg = URLEncoder.encode("Сумма, категория или кошелек указаны неверно.", StandardCharsets.UTF_8);
                    resp.sendRedirect(req.getContextPath() + "/expenses?walletId=" + walletId + "&error=" + errorMsg);
                } catch (NumberFormatException ex) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный ID кошелька.");
                }
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Сумма, категория или кошелек указаны неверно.");
            }
        } catch (ServiceException e) {
            logger.warn("Error adding expense: {}", e.getMessage());
            String walletIdParam = req.getParameter("walletId");
            if (walletIdParam != null && !walletIdParam.trim().isEmpty()) {
                try {
                    long walletId = Long.parseLong(walletIdParam);
                    String errorMsg = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
                    resp.sendRedirect(req.getContextPath() + "/expenses?walletId=" + walletId + "&error=" + errorMsg);
                } catch (NumberFormatException ex) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
                }
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            }
        } catch (Exception e) {
            logger.error("Unexpected error adding expense", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Произошла ошибка при добавлении расхода.");
        }
    }
}
