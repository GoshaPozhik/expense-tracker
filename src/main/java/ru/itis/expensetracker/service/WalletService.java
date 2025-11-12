package ru.itis.expensetracker.service;

import ru.itis.expensetracker.dto.ExpenseDetailDto;
import ru.itis.expensetracker.exception.ServiceException;
import ru.itis.expensetracker.model.Category;
import ru.itis.expensetracker.model.Expense;
import ru.itis.expensetracker.model.Wallet;

import java.math.BigDecimal;
import java.util.List;

public interface WalletService {
    List<Wallet> getWalletsForUser(long userId);
    Wallet getWalletById(long walletId, long userId) throws ServiceException;
    List<ExpenseDetailDto> getDetailedExpensesForWallet(long walletId);
    Expense getExpenseById(long expenseId, long userId) throws ServiceException;
    List<Category> getAvailableCategoriesForUser(long userId);
    boolean hasAccessToWallet(long walletId, long userId);
    void createWallet(String walletName, long userId) throws ServiceException;
    void addExpense(BigDecimal amount, String description, long userId, long walletId, long categoryId) throws ServiceException;
    void updateWallet(long walletId, String walletName, long userId) throws ServiceException;
    void updateExpense(Expense expense, long userId) throws ServiceException;
    void shareWallet(long walletId, long ownerId, String emailToShare) throws ServiceException;
    void deleteExpense(long expenseId, long userId) throws ServiceException;
    void deleteWallet(long walletId, long userId) throws ServiceException;
}
