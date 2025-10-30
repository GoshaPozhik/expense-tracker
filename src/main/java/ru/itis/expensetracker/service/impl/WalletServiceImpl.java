package ru.itis.expensetracker.service.impl;

import ru.itis.expensetracker.dao.CategoryDao;
import ru.itis.expensetracker.dao.ExpenseDao;
import ru.itis.expensetracker.dao.UserDao;
import ru.itis.expensetracker.dao.WalletDao;
import ru.itis.expensetracker.dto.ExpenseDetailDto;
import ru.itis.expensetracker.exception.DaoException;
import ru.itis.expensetracker.exception.ServiceException;
import ru.itis.expensetracker.model.Wallet;
import ru.itis.expensetracker.service.WalletService;
import ru.itis.expensetracker.model.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


public class WalletServiceImpl implements WalletService {

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
        // 1. Получаем все расходы
        List<Expense> expenses = expenseDao.findAllByWalletId(walletId);
        // 2. Преобразуем их в DTO, обогащая данными из других таблиц
        return expenses.stream()
                .map(this::mapToDetailDto)
                .collect(Collectors.toList());
    }

    private ExpenseDetailDto mapToDetailDto(Expense expense) {
        // Запросы к DAO внутри map могут быть неэффективны на больших объемах (N+1 проблема).
        // Для курсового проекта это приемлемо. В продакшене это решается одним сложным JOIN-запросом.
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
        // Возвращаем и общие (дефолтные), и личные категории пользователя
        return categoryDao.findAvailableForUser(userId);
    }

    @Override
    public Expense addExpense(BigDecimal amount, String description, long userId, long walletId, long categoryId) throws ServiceException {
        // Проверка бизнес-правил
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ServiceException("Сумма расхода должна быть положительной.");
        }

        // Проверка, имеет ли пользователь доступ к этому кошельку (важная проверка безопасности!)
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
            return expenseDao.save(expense);
        } catch (DaoException e) {
            throw new ServiceException("Не удалось добавить расход.", e);
        }
    }

    @Override
    public void shareWallet(long walletId, long ownerId, String emailToShare) throws ServiceException {
        // 1. Находим пользователя, с которым хотим поделиться
        User userToShareWith = userDao.findByEmail(emailToShare)
                .orElseThrow(() -> new ServiceException("Пользователь с email '" + emailToShare + "' не найден."));

        // 2. Находим кошелек
        Wallet wallet = walletDao.findById(walletId)
                .orElseThrow(() -> new ServiceException("Кошелек с ID " + walletId + " не найден."));

        // 3. Проверяем права: только владелец может делиться кошельком
        if (!wallet.getOwnerId().equals(ownerId)) {
            throw new ServiceException("Вы не являетесь владельцем этого кошелька и не можете им делиться.");
        }

        // 4. Проверяем, не пытается ли владелец поделиться сам с собой
        if (userToShareWith.getId().equals(ownerId)) {
            throw new ServiceException("Вы не можете поделиться кошельком с самим собой.");
        }

        // 5. Добавляем пользователя в кошелек
        try {
            walletDao.addUserToWallet(userToShareWith.getId(), walletId);
        } catch (DaoException e) {
            throw new ServiceException("Не удалось предоставить доступ к кошельку.", e);
        }
    }

    @Override
    public void deleteExpense(long expenseId, long userId) throws ServiceException {
        // 1. Находим расход по его ID.
        // Optional защищает нас от NullPointerException.
        Expense expense = expenseDao.findById(expenseId)
                .orElseThrow(() -> new ServiceException("Расход с ID " + expenseId + " не найден."));
        // 2. Получаем ID кошелька, к которому принадлежит расход.
        long walletId = expense.getWalletId();
        // 3. ПРОВЕРКА ПРАВ ДОСТУПА. Используем уже существующий метод.
        // Это ключевой шаг для безопасности!
        if (!hasAccessToWallet(walletId, userId)) {
            throw new ServiceException("Доступ запрещен. У вас нет прав на удаление расходов в этом кошельке.");
        }
        // 4. Если все проверки пройдены, удаляем расход.
        expenseDao.delete(expenseId);
    }

    @Override
    public boolean hasAccessToWallet(long walletId, long userId) {
        // 1. Проверяем, не является ли пользователь владельцем
        // Это самая частая операция, поэтому проверяем ее первой
        Optional<Long> ownerIdOpt = walletDao.findOwnerId(walletId);
        if (ownerIdOpt.isPresent() && ownerIdOpt.get() == userId) {
            return true;
        }
        // 2. Если не владелец, проверяем, не расшарен ли ему кошелек
        return walletDao.isSharedWith(walletId, userId);
    }

    @Override
    public void updateExpense(Expense expenseUpdates, long userId) throws ServiceException {
        // 1. Находим существующий расход в БД. Это источник "правды".
        Expense existingExpense = expenseDao.findById(expenseUpdates.getId())
                .orElseThrow(() -> new ServiceException("Расход с ID " + expenseUpdates.getId() + " не найден."));

        // 2. Проверяем права доступа.
        if (!hasAccessToWallet(existingExpense.getWalletId(), userId)) {
            throw new ServiceException("Доступ запрещен.");
        }

        // 3. Обновляем поля существующего объекта данными из объекта-обновления.
        existingExpense.setAmount(expenseUpdates.getAmount());
        existingExpense.setDescription(expenseUpdates.getDescription());
        existingExpense.setCategoryId(expenseUpdates.getCategoryId());

        // При желании можно обновить и дату
        // existingExpense.setExpenseDate(LocalDateTime.now());

        // 4. Сохраняем в БД уже полный, обновленный и безопасный объект.
        expenseDao.update(existingExpense);
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
        // Просто делегируем вызов в DAO
        return categoryDao.findAll();
    }
}
