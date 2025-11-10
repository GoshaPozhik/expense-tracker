package ru.itis.expensetracker.listener;

import ru.itis.expensetracker.dao.CategoryDao;
import ru.itis.expensetracker.dao.ExpenseDao;
import ru.itis.expensetracker.dao.UserDao;
import ru.itis.expensetracker.dao.WalletDao;
import ru.itis.expensetracker.dao.impl.JdbcCategoryDao;
import ru.itis.expensetracker.dao.impl.JdbcExpenseDao;
import ru.itis.expensetracker.dao.impl.JdbcUserDao;
import ru.itis.expensetracker.dao.impl.JdbcWalletDao;
import ru.itis.expensetracker.service.AuthService;
import ru.itis.expensetracker.service.WalletService;
import ru.itis.expensetracker.service.impl.AuthServiceImpl;
import ru.itis.expensetracker.service.impl.WalletServiceImpl;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();

        UserDao userDao = new JdbcUserDao();
        WalletDao walletDao = new JdbcWalletDao();
        CategoryDao categoryDao = new JdbcCategoryDao();
        ExpenseDao expenseDao = new JdbcExpenseDao();

        AuthService authService = new AuthServiceImpl(userDao, walletDao);
        WalletService walletService = new WalletServiceImpl(walletDao, expenseDao, categoryDao, userDao);

        servletContext.setAttribute("authService", authService);
        servletContext.setAttribute("walletService", walletService);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}