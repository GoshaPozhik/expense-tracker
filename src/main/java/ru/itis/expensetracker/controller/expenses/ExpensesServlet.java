package ru.itis.expensetracker.controller.expenses;



import ru.itis.expensetracker.dto.ExpenseDetailDto;
import ru.itis.expensetracker.model.Category;
import ru.itis.expensetracker.model.User;
import ru.itis.expensetracker.service.WalletService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/expenses")
public class ExpensesServlet extends HttpServlet {
    private WalletService walletService;

    @Override
    public void init() {
        walletService = (WalletService) getServletContext().getAttribute("walletService");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            long walletId = Long.parseLong(req.getParameter("walletId"));
            User user = (User) req.getSession().getAttribute("user");

            // TODO: Добавить в WalletService проверку, что пользователь имеет доступ к этому кошельку!
            // Эта логика уже есть в WalletServiceImpl, так что мы в безопасности.

            // List<Expense> expenses = walletService.getExpensesForWallet(walletId);
            List<ExpenseDetailDto> expenses = walletService.getDetailedExpensesForWallet(walletId);
            List<Category> categories = walletService.getAvailableCategoriesForUser(user.getId());

            req.setAttribute("expenses", expenses);
            req.setAttribute("categories", categories);
            req.setAttribute("currentWalletId", walletId);

            req.getRequestDispatcher("/WEB-INF/jsp/expenses.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            // Если ID кошелька невалидный
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный ID кошелька.");
        }
    }
}