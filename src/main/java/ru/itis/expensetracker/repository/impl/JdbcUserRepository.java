package ru.itis.expensetracker.repository.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itis.expensetracker.repository.UserRepository;
import ru.itis.expensetracker.exception.RepositoryException;
import ru.itis.expensetracker.model.User;
import ru.itis.expensetracker.util.DatabaseManager;
import java.sql.*;
import java.util.Optional;

public class JdbcUserRepository implements UserRepository {
    private static final Logger logger = LoggerFactory.getLogger(JdbcUserRepository.class);

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
                throw new RepositoryException("Creating user failed, no rows affected.", null);
            }
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    user.setId(generatedKeys.getLong(1));
                    logger.debug("User saved with ID: {}", user.getId());
                    return user;
                } else {
                    throw new RepositoryException("Creating user failed, no ID obtained.", null);
                }
            }
        } catch (SQLException e) {
            logger.error("Error saving user: {}", user.getEmail(), e);
            throw new RepositoryException("Error saving user: " + user.getEmail(), e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_EMAIL_SQL)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    logger.debug("User found by email: {}", email);
                    return Optional.of(mapRowToUser(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user by email: {}", email, e);
            throw new RepositoryException("Error finding user by email: " + email, e);
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
                    logger.debug("User found by ID: {}", id);
                    return Optional.of(mapRowToUser(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding user by id: {}", id, e);
            throw new RepositoryException("Error finding user by id: " + id, e);
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
