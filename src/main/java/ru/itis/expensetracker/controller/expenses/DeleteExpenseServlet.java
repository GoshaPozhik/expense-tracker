package ru.itis.expensetracker.controller.expenses;



import ru.itis.expensetracker.exception.ServiceException;
import ru.itis.expensetracker.model.User;
import ru.itis.expensetracker.service.WalletService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/expenses/delete")
public class DeleteExpenseServlet extends HttpServlet {
    private WalletService walletService;

    @Override
    public void init() {
        walletService = (WalletService) getServletContext().getAttribute("walletService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String walletId = req.getParameter("walletId");
        String redirectUrl = req.getContextPath() + "/expenses?walletId=" + walletId;

        try {
            long expenseId = Long.parseLong(req.getParameter("expenseId"));
            User user = (User) req.getSession().getAttribute("user");

            // Сервис должен проверить, имеет ли право user удалять этот расход
            walletService.deleteExpense(expenseId, user.getId());

        } catch (NumberFormatException e) {
            // Если ID невалидный
            String errorMsg = URLEncoder.encode("Некорректный ID для удаления.", StandardCharsets.UTF_8);
            redirectUrl += "&error=" + errorMsg;
        } catch (ServiceException e) {
            // Если нет прав на удаление
            String errorMsg = URLEncoder.encode(e.getMessage(), StandardCharsets.UTF_8);
            redirectUrl += "&error=" + errorMsg;
        }

        // Используем Post-Redirect-Get, чтобы вернуться на страницу расходов
        resp.sendRedirect(redirectUrl);
    }
}