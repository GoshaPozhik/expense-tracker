package ru.itis.expensetracker.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itis.expensetracker.dao.ExpenseDao;
import ru.itis.expensetracker.exception.DaoException;
import ru.itis.expensetracker.model.Expense;
import ru.itis.expensetracker.util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcExpenseDao implements ExpenseDao {
    private static final Logger logger = LoggerFactory.getLogger(JdbcExpenseDao.class);

    private static final String SAVE_SQL = "INSERT INTO expenses (amount, description, expense_date, user_id, wallet_id, category_id) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String FIND_BY_WALLET_SQL = "SELECT id, amount, description, expense_date, user_id, wallet_id, category_id FROM expenses WHERE wallet_id = ? ORDER BY expense_date DESC";
    private static final String FIND_BY_ID_SQL = "SELECT id, amount, description, expense_date, user_id, wallet_id, category_id FROM expenses WHERE id = ?";
    private static final String UPDATE_SQL = "UPDATE expenses SET amount = ?, description = ?, category_id = ?, expense_date = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM expenses WHERE id = ?";

    @Override
    public Expense save(Expense expense) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setBigDecimal(1, expense.getAmount());
            statement.setString(2, expense.getDescription());
            statement.setTimestamp(3, Timestamp.valueOf(expense.getExpenseDate()));
            statement.setLong(4, expense.getUserId());
            statement.setLong(5, expense.getWalletId());
            statement.setLong(6, expense.getCategoryId());

            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    expense.setId(keys.getLong(1));
                    logger.debug("Expense saved with ID: {}, walletId: {}", expense.getId(), expense.getWalletId());
                    return expense;
                }
            }
            throw new DaoException("Failed to save expense, no ID obtained.", null);
        } catch (SQLException e) {
            logger.error("Error saving expense", e);
            throw new DaoException("Error saving expense", e);
        }
    }

    @Override
    public List<Expense> findAllByWalletId(long walletId) {
        List<Expense> expenses = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_WALLET_SQL)) {
            statement.setLong(1, walletId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    expenses.add(mapRowToExpense(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding expenses for wallet {}", walletId, e);
            throw new DaoException("Error finding expenses for wallet " + walletId, e);
        }
        logger.debug("Found {} expenses for wallet {}", expenses.size(), walletId);
        return expenses;
    }

    @Override
    public Optional<Expense> findById(long id) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    logger.debug("Expense found by ID: {}", id);
                    return Optional.of(mapRowToExpense(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding expense by id {}", id, e);
            throw new DaoException("Error finding expense by id " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public void update(Expense expense) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setBigDecimal(1, expense.getAmount());
            statement.setString(2, expense.getDescription());
            statement.setLong(3, expense.getCategoryId());
            statement.setTimestamp(4, Timestamp.valueOf(expense.getExpenseDate()));
            statement.setLong(5, expense.getId());
            int updated = statement.executeUpdate();
            logger.debug("Expense updated: id={}, rows affected={}", expense.getId(), updated);
        } catch (SQLException e) {
            logger.error("Error updating expense with id {}", expense.getId(), e);
            throw new DaoException("Error updating expense with id " + expense.getId(), e);
        }
    }

    @Override
    public void delete(long id) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setLong(1, id);
            int deleted = statement.executeUpdate();
            logger.debug("Expense deleted: id={}, rows affected={}", id, deleted);
        } catch (SQLException e) {
            logger.error("Error deleting expense with id {}", id, e);
            throw new DaoException("Error deleting expense with id " + id, e);
        }
    }

    private Expense mapRowToExpense(ResultSet rs) throws SQLException {
        return Expense.builder()
                .id(rs.getLong("id"))
                .amount(rs.getBigDecimal("amount"))
                .description(rs.getString("description"))
                .expenseDate(rs.getTimestamp("expense_date").toLocalDateTime())
                .userId(rs.getLong("user_id"))
                .walletId(rs.getLong("wallet_id"))
                .categoryId(rs.getLong("category_id"))
                .build();
    }
}
