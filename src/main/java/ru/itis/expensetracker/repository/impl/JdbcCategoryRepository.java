package ru.itis.expensetracker.repository.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itis.expensetracker.repository.CategoryRepository;
import ru.itis.expensetracker.exception.DaoException;
import ru.itis.expensetracker.model.Category;
import ru.itis.expensetracker.util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCategoryRepository implements CategoryRepository {
    private static final Logger logger = LoggerFactory.getLogger(JdbcCategoryRepository.class);

    private static final String SAVE_SQL = "INSERT INTO categories (name, user_id) VALUES (?, ?)";
    private static final String FIND_BY_ID_SQL = "SELECT id, name, user_id FROM categories WHERE id = ?";
    private static final String FIND_AVAILABLE_SQL = "SELECT id, name, user_id FROM categories WHERE user_id = ? OR user_id IS NULL";
    private static final String FIND_ALL_SQL = "SELECT id, name, user_id FROM categories";
    private static final String UPDATE_SQL = "UPDATE categories SET name = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM categories WHERE id = ?";

    @Override
    public Category save(Category category) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, category.getName());
            if (category.getUserId() == null) {
                statement.setNull(2, Types.BIGINT);
            } else {
                statement.setLong(2, category.getUserId());
            }
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    category.setId(keys.getLong(1));
                    logger.debug("Category saved with ID: {}, name: {}", category.getId(), category.getName());
                    return category;
                }
            }
            throw new DaoException("Failed to save category, no ID obtained.", null);
        } catch (SQLException e) {
            logger.error("Error saving category", e);
            throw new DaoException("Error saving category", e);
        }
    }
    @Override
    public Optional<Category> findById(long id) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    logger.debug("Category found by ID: {}", id);
                    return Optional.of(mapRowToCategory(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding category by id {}", id, e);
            throw new DaoException("Error finding category by id " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public List<Category> findAvailableForUser(long userId) {
        List<Category> categories = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_AVAILABLE_SQL)) {
            statement.setLong(1, userId);
            try (ResultSet rs = statement.executeQuery()) {
                while (rs.next()) {
                    categories.add(mapRowToCategory(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding available categories for user {}", userId, e);
            throw new DaoException("Error finding available categories for user " + userId, e);
        }
        logger.debug("Found {} available categories for user {}", categories.size(), userId);
        return categories;
    }

    @Override
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL);
             ResultSet rs = statement.executeQuery()) {
            while (rs.next()) {
                categories.add(mapRowToCategory(rs));
            }
        } catch (SQLException e) {
            throw new DaoException("Error finding all categories", e);
        }
        return categories;
    }

    @Override
    public void update(Category category) {
        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(UPDATE_SQL)) {
            statement.setString(1, category.getName());
            statement.setLong(2, category.getId());
            int updated = statement.executeUpdate();
            logger.debug("Category updated: id={}, name={}, rows affected={}", category.getId(), category.getName(), updated);
        } catch (SQLException e) {
            logger.error("Error updating category with id {}", category.getId(), e);
            throw new DaoException("Error updating category with id " + category.getId(), e);
        }
    }
    @Override
    public void delete(long id) {
        try (Connection connection = DatabaseManager.getConnection();
            PreparedStatement statement = connection.prepareStatement(DELETE_SQL)) {
            statement.setLong(1, id);
            int deleted = statement.executeUpdate();
            logger.debug("Category deleted: id={}, rows affected={}", id, deleted);
        } catch (SQLException e) {
            logger.error("Error deleting category with id {}", id, e);
            throw new DaoException("Error deleting category with id " + id, e);
        }
    }

    private Category mapRowToCategory(ResultSet rs) throws SQLException {
        return Category.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .userId(rs.getObject("user_id", Long.class))
                .build();
    }
}