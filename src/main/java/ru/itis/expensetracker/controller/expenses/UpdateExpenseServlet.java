package ru.itis.expensetracker.controller.expenses;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itis.expensetracker.exception.ServiceException;
import ru.itis.expensetracker.model.Category;
import ru.itis.expensetracker.model.Expense;
import ru.itis.expensetracker.model.User;
import ru.itis.expensetracker.service.WalletService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet("/expenses/edit")
public class UpdateExpenseServlet extends HttpServlet {
    private static final Logger logger = LoggerFactory.getLogger(UpdateExpenseServlet.class);
    private WalletService walletService;

    @Override
    public void init() {
        walletService = (WalletService) getServletContext().getAttribute("walletService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            long expenseId = Long.parseLong(req.getParameter("id"));
            User user = (User) req.getSession().getAttribute("user");
            Expense expense = walletService.getExpenseById(expenseId, user.getId());
            List<Category> categories = walletService.getAvailableCategoriesForUser(user.getId());

            req.setAttribute("expense", expense);
            req.setAttribute("categories", categories);
            logger.debug("Loading expense {} for edit by user {}", expenseId, user.getId());
            req.getRequestDispatcher("/WEB-INF/jsp/expenses/edit.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            logger.warn("Invalid expense ID format: {}", req.getParameter("id"));
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный ID расхода.");
        } catch (ServiceException e) {
            logger.warn("Error loading expense for edit: {}", e.getMessage());
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String walletIdParam = req.getParameter("walletId");
            String expenseIdParam = req.getParameter("expenseId");
            String amountParam = req.getParameter("amount");
            String categoryIdParam = req.getParameter("categoryId");
            String description = req.getParameter("description");
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

            if (amountParam == null || amountParam.trim().isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Сумма не может быть пустой.");
                return;
            }

            if (categoryIdParam == null || categoryIdParam.trim().isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Категория не выбрана.");
                return;
            }

            long walletId = Long.parseLong(walletIdParam);
            long expenseId = Long.parseLong(expenseIdParam);
            BigDecimal amount = new BigDecimal(amountParam);
            long categoryId = Long.parseLong(categoryIdParam);

            Expense expenseToUpdate = Expense.builder()
                    .id(expenseId)
                    .amount(amount)
                    .description(description)
                    .categoryId(categoryId)
                    .build();

            walletService.updateExpense(expenseToUpdate, user.getId());
            logger.info("Expense updated: expenseId={}, walletId={}, userId={}", expenseId, walletId, user.getId());
            resp.sendRedirect(req.getContextPath() + "/expenses?walletId=" + walletId);
        } catch (NumberFormatException e) {
            String walletIdParam = req.getParameter("walletId");
            if (walletIdParam != null && !walletIdParam.trim().isEmpty()) {
                try {
                    long walletId = Long.parseLong(walletIdParam);
                    String errorMsg = URLEncoder.encode("Некорректные данные для обновления.", StandardCharsets.UTF_8);
                    resp.sendRedirect(req.getContextPath() + "/expenses?walletId=" + walletId + "&error=" + errorMsg);
                } catch (NumberFormatException ex) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный ID кошелька.");
                }
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректные данные для обновления.");
            }
        } catch (ServiceException e) {
            logger.warn("Error updating expense: {}", e.getMessage());
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
            logger.error("Unexpected error updating expense", e);
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Произошла ошибка при обновлении расхода.");
        }
    }
}
