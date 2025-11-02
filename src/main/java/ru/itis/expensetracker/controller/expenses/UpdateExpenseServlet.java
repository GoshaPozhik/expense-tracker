package ru.itis.expensetracker.controller.expenses;



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
import java.util.List;

@WebServlet("/expenses/edit")
public class UpdateExpenseServlet extends HttpServlet {
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

            // ИСПРАВЛЕНИЕ: используем категории, доступные пользователю
            List<Category> categories = walletService.getAvailableCategoriesForUser(user.getId());

            req.setAttribute("expense", expense);
            req.setAttribute("categories", categories);
            req.getRequestDispatcher("/WEB-INF/jsp/expenses/edit.jsp").forward(req, resp);
        } catch (NumberFormatException | ServiceException e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String walletId = req.getParameter("walletId");

        try {
            User user = (User) req.getSession().getAttribute("user");

            // ИСПОЛЬЗУЕМ BUILDER для создания объекта Expense
            Expense expenseToUpdate = Expense.builder()
                    .id(Long.parseLong(req.getParameter("expenseId")))
                    .amount(new BigDecimal(req.getParameter("amount")))
                    .description(req.getParameter("description"))
                    .categoryId(Long.parseLong(req.getParameter("categoryId")))
                    .build(); // Создаем объект

            // Важно: мы передаем в сервис только те поля, которые пользователь может менять.
            // ID пользователя и кошелька сервис должен проверить и подставить сам для безопасности.

            walletService.updateExpense(expenseToUpdate, user.getId());

            resp.sendRedirect(req.getContextPath() + "/expenses?walletId=" + walletId);

        } catch (ServiceException | NumberFormatException | NullPointerException e) {
            // Добавляем обработку NPE на случай невалидных данных с формы
            resp.sendRedirect(req.getContextPath() + "/expenses?walletId=" + walletId + "&error=UpdateFailed");
        }
    }
}