package ru.itis.expensetracker.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itis.expensetracker.repository.CategoryRepository;
import ru.itis.expensetracker.repository.ExpenseRepository;
import ru.itis.expensetracker.repository.UserRepository;
import ru.itis.expensetracker.repository.WalletRepository;
import ru.itis.expensetracker.repository.impl.JdbcCategoryRepository;
import ru.itis.expensetracker.repository.impl.JdbcExpenseRepository;
import ru.itis.expensetracker.repository.impl.JdbcUserRepository;
import ru.itis.expensetracker.repository.impl.JdbcWalletRepository;
import ru.itis.expensetracker.service.AuthService;
import ru.itis.expensetracker.service.CategoryService;
import ru.itis.expensetracker.service.WalletService;
import ru.itis.expensetracker.service.impl.AuthServiceImpl;
import ru.itis.expensetracker.service.impl.CategoryServiceImpl;
import ru.itis.expensetracker.service.impl.WalletServiceImpl;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class ContextListener implements ServletContextListener {
    private static final Logger logger = LoggerFactory.getLogger(ContextListener.class);

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();

        UserRepository userRepository = new JdbcUserRepository();
        WalletRepository walletRepository = new JdbcWalletRepository();
        CategoryRepository categoryRepository = new JdbcCategoryRepository();
        ExpenseRepository expenseRepository = new JdbcExpenseRepository();

        AuthService authService = new AuthServiceImpl(userRepository, walletRepository);
        WalletService walletService = new WalletServiceImpl(walletRepository, expenseRepository, categoryRepository, userRepository);
        CategoryService categoryService = new CategoryServiceImpl(categoryRepository);

        servletContext.setAttribute("authService", authService);
        servletContext.setAttribute("walletService", walletService);
        servletContext.setAttribute("categoryService", categoryService);
        
        logger.info("Application context initialized. Services registered: authService, walletService, categoryService");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        logger.info("Application context destroyed");
    }
}