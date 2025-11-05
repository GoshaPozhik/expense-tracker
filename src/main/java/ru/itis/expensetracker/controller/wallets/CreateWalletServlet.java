package ru.itis.expensetracker.controller.wallets;

import ru.itis.expensetracker.dao.WalletDao;
import ru.itis.expensetracker.dao.impl.JdbcWalletDao;
import ru.itis.expensetracker.model.User;
import ru.itis.expensetracker.model.Wallet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/wallets/create")
public class CreateWalletServlet extends HttpServlet {
    private WalletDao walletDao; // Используем DAO, т.к. логика проста

    @Override
    public void init() {
        // Получим walletDao из контекста, как и сервисы
        // Для этого его тоже нужно положить в ContextListener
        walletDao = (WalletDao) getServletContext().getAttribute("walletDao");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (walletDao == null) { // Нужно нормально настроить это потому что пока это показывает нулл и зависимость жддбс подключается лишняя
            walletDao = new JdbcWalletDao(); // или другая имплементация
        }
        String walletName = req.getParameter("walletName");
        User user = (User) req.getSession().getAttribute("user");

        if (walletName != null && !walletName.isBlank()) {
            Wallet newWallet = Wallet.builder()
                    .name(walletName)
                    .ownerId(user.getId())
                    .build();
            walletDao.save(newWallet);
        }

        // Перенаправляем на главную страницу, где появится новый кошелек
        resp.sendRedirect(req.getContextPath() + "/home");
    }
}