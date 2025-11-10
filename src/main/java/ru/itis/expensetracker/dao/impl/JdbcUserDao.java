package ru.itis.expensetracker.dao.impl;

import ru.itis.expensetracker.dao.UserDao;
import ru.itis.expensetracker.exception.DaoException;
import ru.itis.expensetracker.model.User;
import ru.itis.expensetracker.util.DatabaseManager;
import java.sql.*;
import java.util.Optional;

public class JdbcUserDao implements UserDao {

    private static final String SAVE_SQL = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";
    private static final String FIND_BY_EMAIL_SQL = "SELECT id, username, email, password_hash FROM users WHERE email = ?";
    private static final String FIND_BY_ID_SQL = "SELECT id, username, email, password_hash FROM users WHERE id = ?";

    @Override
    public User save(User user) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(SAVE_SQL, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, user.getUsername());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPasswordHash());
            int affectedRows = statement.executeUpdate();
            if (affectedRows == 0) {
                throw new DaoException("Creating user failed, no rows affected.", null);
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                    return user;
                } else {
                    throw new DaoException("Creating user failed, no ID obtained.", null);
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error saving user: " + user.getEmail(), e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_EMAIL_SQL)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowToUser(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error finding user by email: " + email, e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<User> findById(long id) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setLong(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapRowToUser(resultSet));
                }
            }
        } catch (SQLException e) {
            throw new DaoException("Error finding user by id: " + id, e);
        }
        return Optional.empty();
    }

    private User mapRowToUser(ResultSet resultSet) throws SQLException {
        return User.builder()
                .id(resultSet.getLong("id"))
                .username(resultSet.getString("username"))
                .email(resultSet.getString("email"))
                .passwordHash(resultSet.getString("password_hash"))
                .build();
    }
}
