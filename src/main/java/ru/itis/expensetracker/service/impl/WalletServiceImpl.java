package ru.itis.expensetracker.service.impl;

import ru.itis.expensetracker.dao.CategoryDao;
import ru.itis.expensetracker.dao.ExpenseDao;
import ru.itis.expensetracker.dao.UserDao;
import ru.itis.expensetracker.dao.WalletDao;
import ru.itis.expensetracker.dto.ExpenseDetailDto;
import ru.itis.expensetracker.exception.DaoException;
import ru.itis.expensetracker.exception.ServiceException;
import ru.itis.expensetracker.model.Category;
import ru.itis.expensetracker.model.Expense;
import ru.itis.expensetracker.model.User;
import ru.itis.expensetracker.model.Wallet;
import ru.itis.expensetracker.service.WalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class WalletServiceImpl implements WalletService {
    private static final Logger logger = LoggerFactory.getLogger(WalletServiceImpl.class);

    private final WalletDao walletDao;
    private final ExpenseDao expenseDao;
    private final CategoryDao categoryDao;
    private final UserDao userDao;

    public WalletServiceImpl(WalletDao walletDao, ExpenseDao expenseDao, CategoryDao categoryDao, UserDao userDao) {
        this.walletDao = walletDao;
        this.expenseDao = expenseDao;
        this.categoryDao = categoryDao;
        this.userDao = userDao;
    }

    @Override
    public List<Wallet> getWalletsForUser(long userId) {
        return walletDao.findAllByUserId(userId);
    }

    @Override
    public List<Expense> getExpensesForWallet(long walletId) {
        return expenseDao.findAllByWalletId(walletId);
    }

    @Override
    public List<ExpenseDetailDto> getDetailedExpensesForWallet(long walletId) {
        List<Expense> expenses = expenseDao.findAllByWalletId(walletId);
        return expenses.stream()
                .map(this::mapToDetailDto)
                .collect(Collectors.toList());
    }

    private ExpenseDetailDto mapToDetailDto(Expense expense) {
        String categoryName = categoryDao.findById(expense.getCategoryId())
                .map(Category::getName)
                .orElse("Без категории");
        String userName = userDao.findById(expense.getUserId())
                .map(User::getUsername)
                .orElse("Неизвестный пользователь");
        return ExpenseDetailDto.builder()
                .id(expense.getId())
                .amount(expense.getAmount())
                .description(expense.getDescription())
                .expenseDate(expense.getExpenseDate())
                .categoryName(categoryName)
                .userName(userName)
                .build();
    }

    @Override
    public List<Category> getAvailableCategoriesForUser(long userId) {
        return categoryDao.findAvailableForUser(userId);
    }

    @Override
    public Expense addExpense(BigDecimal amount, String description, long userId, long walletId, long categoryId) throws ServiceException {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("Сумма расхода должна быть положительной.");
        }

        boolean hasAccess = walletDao.findAllByUserId(userId).stream()
                .anyMatch(wallet -> wallet.getId().equals(walletId));
        if (!hasAccess) {
            throw new ServiceException("Доступ к кошельку с ID " + walletId + " запрещен.");
        }

        Expense expense = Expense.builder()
                .amount(amount)
                .description(description)
                .expenseDate(LocalDateTime.now())
                .userId(userId)
                .walletId(walletId)
                .categoryId(categoryId)
                .build();
        try {
            Expense saved = expenseDao.save(expense);
            logger.debug("Expense added: id={}, amount={}, walletId={}, userId={}", 
                    saved.getId(), amount, walletId, userId);
            return saved;
        } catch (DaoException e) {
            logger.error("Error adding expense", e);
            throw new ServiceException("Не удалось добавить расход.", e);
        }
    }

    @Override
    public void shareWallet(long walletId, long ownerId, String emailToShare) throws ServiceException {
        User userToShareWith = userDao.findByEmail(emailToShare)
                .orElseThrow(() -> new ServiceException("Пользователь с email '" + emailToShare + "' не найден."));

        Wallet wallet = walletDao.findById(walletId)
                .orElseThrow(() -> new ServiceException("Кошелек с ID " + walletId + " не найден."));

        if (!wallet.getOwnerId().equals(ownerId)) {
            throw new ServiceException("Вы не являетесь владельцем этого кошелька и не можете им делиться.");
        }

        if (userToShareWith.getId().equals(ownerId)) {
            throw new ServiceException("Вы не можете поделиться кошельком с самим собой.");
        }

        try {
            walletDao.addUserToWallet(userToShareWith.getId(), walletId);
            logger.debug("Wallet shared: walletId={}, ownerId={}, sharedWithUserId={}", 
                    walletId, ownerId, userToShareWith.getId());
        } catch (DaoException e) {
            logger.error("Error sharing wallet", e);
            throw new ServiceException("Не удалось предоставить доступ к кошельку.", e);
        }
    }

    @Override
    public void deleteExpense(long expenseId, long userId) throws ServiceException {
        Expense expense = expenseDao.findById(expenseId)
                .orElseThrow(() -> new ServiceException("Расход с ID " + expenseId + " не найден."));
        long walletId = expense.getWalletId();
        if (!hasAccessToWallet(walletId, userId)) {
            logger.warn("Access denied for expense deletion: expenseId={}, userId={}, walletId={}", 
                    expenseId, userId, walletId);
            throw new ServiceException("Доступ запрещен. У вас нет прав на удаление расходов в этом кошельке.");
        }
        expenseDao.delete(expenseId);
        logger.debug("Expense deleted: expenseId={}, walletId={}, userId={}", expenseId, walletId, userId);
    }

    @Override
    public boolean hasAccessToWallet(long walletId, long userId) {
        Optional<Long> ownerIdOpt = walletDao.findOwnerId(walletId);
        if (ownerIdOpt.isPresent() && ownerIdOpt.get() == userId) {
            return true;
        }
        return walletDao.isSharedWith(walletId, userId);
    }

    @Override
    public void updateExpense(Expense expenseUpdates, long userId) throws ServiceException {
        Expense existingExpense = expenseDao.findById(expenseUpdates.getId())
                .orElseThrow(() -> new ServiceException("Расход с ID " + expenseUpdates.getId() + " не найден."));

        if (!hasAccessToWallet(existingExpense.getWalletId(), userId)) {
            throw new ServiceException("Доступ запрещен.");
        }

        existingExpense.setAmount(expenseUpdates.getAmount());
        existingExpense.setDescription(expenseUpdates.getDescription());
        existingExpense.setCategoryId(expenseUpdates.getCategoryId());

        expenseDao.update(existingExpense);
        logger.debug("Expense updated: expenseId={}, walletId={}, userId={}", 
                existingExpense.getId(), existingExpense.getWalletId(), userId);
    }

    @Override
    public Expense getExpenseById(long expenseId, long userId) throws ServiceException {
        Expense expense = expenseDao.findById(expenseId)
                .orElseThrow(() -> new ServiceException("Расход не найден."));
        if (!hasAccessToWallet(expense.getWalletId(), userId)) {
            throw new ServiceException("Доступ запрещен.");
        }
        return expense;
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryDao.findAll();
    }

    @Override
    public Wallet createWallet(String walletName, long userId) throws ServiceException {
        if (walletName == null || walletName.trim().isEmpty()) {
            throw new ServiceException("Название кошелька не может быть пустым.");
        }

        if (userDao.findById(userId).isEmpty()) {
            throw new ServiceException("Пользователь не найден.");
        }

        Wallet wallet = Wallet.builder()
                .name(walletName.trim())
                .ownerId(userId)
                .build();

        try {
            Wallet saved = walletDao.save(wallet);
            logger.debug("Wallet created: id={}, name={}, ownerId={}", 
                    saved.getId(), saved.getName(), saved.getOwnerId());
            return saved;
        } catch (DaoException e) {
            logger.error("Error creating wallet", e);
            throw new ServiceException("Не удалось создать кошелек.", e);
        }
    }

    @Override
    public Wallet getWalletById(long walletId, long userId) throws ServiceException {
        Wallet wallet = walletDao.findById(walletId)
                .orElseThrow(() -> new ServiceException("Кошелек с ID " + walletId + " не найден."));

        if (!hasAccessToWallet(walletId, userId)) {
            throw new ServiceException("Доступ к кошельку запрещен.");
        }

        return wallet;
    }

    @Override
    public void updateWallet(long walletId, String walletName, long userId) throws ServiceException {
        Wallet wallet = walletDao.findById(walletId)
                .orElseThrow(() -> new ServiceException("Кошелек с ID " + walletId + " не найден."));

        // Только владелец может редактировать кошелек
        if (!wallet.getOwnerId().equals(userId)) {
            throw new ServiceException("Только владелец может редактировать кошелек.");
        }

        if (walletName == null || walletName.trim().isEmpty()) {
            throw new ServiceException("Название кошелька не может быть пустым.");
        }

        String trimmedName = walletName.trim();
        if (trimmedName.length() > 100) {
            throw new ServiceException("Название кошелька не может превышать 100 символов.");
        }

        wallet.setName(trimmedName);

        try {
            walletDao.update(wallet);
            logger.info("Wallet updated: {} by user: {}", walletId, userId);
        } catch (DaoException e) {
            logger.error("Error updating wallet: {}", walletId, e);
            throw new ServiceException("Не удалось обновить кошелек.", e);
        }
    }

    @Override
    public void deleteWallet(long walletId, long userId) throws ServiceException {
        Wallet wallet = walletDao.findById(walletId)
                .orElseThrow(() -> new ServiceException("Кошелек с ID " + walletId + " не найден."));

        // Только владелец может удалить кошелек
        if (!wallet.getOwnerId().equals(userId)) {
            throw new ServiceException("Только владелец может удалить кошелек.");
        }

        try {
            // Удаление кошелька автоматически удалит все связанные расходы (CASCADE)
            walletDao.delete(walletId);
            logger.info("Wallet deleted: {} by user: {}", walletId, userId);
        } catch (DaoException e) {
            logger.error("Error deleting wallet: {}", walletId, e);
            throw new ServiceException("Не удалось удалить кошелек.", e);
        }
    }
}
