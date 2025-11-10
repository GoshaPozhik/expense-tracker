package ru.itis.expensetracker.dao;

import ru.itis.expensetracker.model.Expense;
import java.util.List;
import java.util.Optional;

public interface ExpenseDao {
    Expense save(Expense expense);
    List<Expense> findAllByWalletId(long walletId);
    Optional<Expense> findById(long id);
    void delete(long id);
    void update(Expense expense);
}