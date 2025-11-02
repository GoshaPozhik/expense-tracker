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
import java.math.BigDecimal;

@WebServlet("/expenses/add")
public class AddExpenseServlet extends HttpServlet {
    private WalletService walletService;

    @Override
    public void init() {
        walletService = (WalletService) getServletContext().getAttribute("walletService");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            // 1. Получаем данные из формы
            BigDecimal amount = new BigDecimal(req.getParameter("amount"));
            String description = req.getParameter("description");
            long categoryId = Long.parseLong(req.getParameter("categoryId"));
            long walletId = Long.parseLong(req.getParameter("walletId"));
            User user = (User) req.getSession().getAttribute("user");

            // 2. Вызываем сервис
            walletService.addExpense(amount, description, user.getId(), walletId, categoryId);

            // 3. Post-Redirect-Get (PRG) паттерн: перенаправляем, чтобы избежать повторной отправки формы
            resp.sendRedirect(req.getContextPath() + "/expenses?walletId=" + walletId);

        } catch (NumberFormatException e) {
            // Если данные некорректны
            req.setAttribute("error", "Сумма, категория или кошелек указаны неверно.");
            // Перенаправляем обратно на страницу с расходами, но с ошибкой
            // Лучше было бы сохранить введенные данные, чтобы пользователю не пришлось вводить их заново
            resp.sendRedirect(req.getContextPath() + "/expenses?walletId=" + req.getParameter("walletId") + "&error=invalid_data");
        } catch (ServiceException e) {
            // Если бизнес-логика не прошла (например, нет доступа)
            req.setAttribute("error", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/expenses?walletId=" + req.getParameter("walletId") + "&error=" + e.getMessage());
        }
    }
}