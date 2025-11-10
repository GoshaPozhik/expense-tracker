package ru.itis.expensetracker.controller.expenses;

import ru.itis.expensetracker.dto.ExpenseDetailDto;
import ru.itis.expensetracker.model.Category;
import ru.itis.expensetracker.model.User;
import ru.itis.expensetracker.service.WalletService;

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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            String walletIdParam = req.getParameter("walletId");
            User user = (User) req.getSession().getAttribute("user");

            if (user == null) {
                resp.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Пользователь не авторизован.");
                return;
            }

            if (walletIdParam == null || walletIdParam.trim().isEmpty()) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID кошелька не указан.");
                return;
            }

            long walletId = Long.parseLong(walletIdParam);

            if (!walletService.hasAccessToWallet(walletId, user.getId())) {
                resp.sendError(HttpServletResponse.SC_FORBIDDEN, "У вас нет доступа к этому кошельку.");
                return;
            }

            List<ExpenseDetailDto> expenses = walletService.getDetailedExpensesForWallet(walletId);
            List<Category> categories = walletService.getAvailableCategoriesForUser(user.getId());

            req.setAttribute("expenses", expenses);
            req.setAttribute("categories", categories);
            req.setAttribute("currentWalletId", walletId);

            req.getRequestDispatcher("/WEB-INF/jsp/expenses/list.jsp").forward(req, resp);
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Некорректный ID кошелька.");
        } catch (Exception e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Произошла ошибка при загрузке расходов.");
        }
    }
}
