package ru.itis.expensetracker.repository.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itis.expensetracker.repository.WalletRepository;
import ru.itis.expensetracker.exception.DaoException;
import ru.itis.expensetracker.model.Wallet;
import ru.itis.expensetracker.util.DatabaseManager;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcWalletRepository implements WalletRepository {
    private static final Logger logger = LoggerFactory.getLogger(JdbcWalletRepository.class);

    private static final String SAVE_WALLET_SQL = "INSERT INTO wallets (name, owner_id) VALUES (?, ?)";
    private static final String ADD_USER_TO_WALLET_SQL = "INSERT INTO user_wallets (user_id, wallet_id) VALUES (?, ?)";
    private static final String FIND_BY_ID_SQL = "SELECT id, name, owner_id FROM wallets WHERE id = ?";
    private static final String FIND_ALL_BY_USER_ID_SQL =
            "SELECT w.id, w.name, w.owner_id FROM wallets w " +
                    "JOIN user_wallets uw ON w.id = uw.wallet_id " +
                    "WHERE uw.user_id = ?";
    private static final String IS_SHARED_WITH_SQL = "SELECT 1 FROM user_wallets WHERE wallet_id = ? AND user_id = ?";
    private static final String FIND_OWNER_ID_SQL = "SELECT owner_id FROM wallets WHERE id = ?";
    private static final String UPDATE_SQL = "UPDATE wallets SET name = ? WHERE id = ?";
    private static final String DELETE_SQL = "DELETE FROM wallets WHERE id = ?";

    @Override
    public Wallet save(Wallet wallet) {
        Connection connection = null;
        try {
            connection = DatabaseManager.getConnection();
            connection.setAutoCommit(false);
            try (PreparedStatement walletStmt = connection.prepareStatement(SAVE_WALLET_SQL, Statement.RETURN_GENERATED_KEYS)) {
                walletStmt.setString(1, wallet.getName());
                walletStmt.setLong(2, wallet.getOwnerId());
                walletStmt.executeUpdate();
                try (ResultSet generatedKeys = walletStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        wallet.setId(generatedKeys.getLong(1));
                    } else {
                        throw new SQLException("Creating wallet failed, no ID obtained.");
                    }
                }
            }
            addUserToWalletInternal(connection, wallet.getOwnerId(), wallet.getId());
            connection.commit();
            logger.debug("Wallet saved with ID: {}, ownerId: {}", wallet.getId(), wallet.getOwnerId());
            return wallet;
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                    logger.warn("Transaction rolled back for wallet save");
                } catch (SQLException ex) {
                    logger.error("Error during transaction rollback", ex);
                    throw new DaoException("Error during transaction rollback", ex);
                }
            }
            logger.error("Error saving wallet", e);
            throw new DaoException("Error saving wallet", e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    @Override
    public void addUserToWallet(long userId, long walletId) {
        try (Connection connection = DatabaseManager.getConnection()) {
            addUserToWalletInternal(connection, userId, walletId);
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) {
                System.out.println("User " + userId + " is already in wallet " + walletId);
            } else {
                throw new DaoException("Error adding user to wallet", e);
            }
        }
    }
    private void addUserToWalletInternal(Connection connection, long userId, long walletId) throws SQLException {
        try (PreparedStatement linkStmt = connection.prepareStatement(ADD_USER_TO_WALLET_SQL)) {
            linkStmt.setLong(1, userId);
            linkStmt.setLong(2, walletId);
            linkStmt.executeUpdate();
        }
    }

    @Override
    public List<Wallet> findAllByUserId(long userId) {
        List<Wallet> wallets = new ArrayList<>();
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_BY_USER_ID_SQL)) {
            statement.setLong(1, userId);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    wallets.add(mapRowToWallet(resultSet));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding wallets for user {}", userId, e);
            throw new DaoException("Error finding wallets for user " + userId, e);
        }
        logger.debug("Found {} wallets for user {}", wallets.size(), userId);
        return wallets;
    }

    @Override
    public Optional<Wallet> findById(long id) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(FIND_BY_ID_SQL)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    logger.debug("Wallet found by ID: {}", id);
                    return Optional.of(mapRowToWallet(rs));
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding wallet by id: {}", id, e);
            throw new DaoException("Error finding wallet by id: " + id, e);
        }
        return Optional.empty();
    }

    @Override
    public void update(Wallet wallet) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, wallet.getName());
            stmt.setLong(2, wallet.getId());
            int updated = stmt.executeUpdate();
            if (updated == 0) {
                logger.warn("No wallet updated with id {}", wallet.getId());
                throw new DaoException("No wallet updated with id " + wallet.getId(), null);
            }
            logger.debug("Wallet updated: id={}, name={}", wallet.getId(), wallet.getName());
        } catch (SQLException e) {
            logger.error("Error updating wallet with id {}", wallet.getId(), e);
            throw new DaoException("Error updating wallet with id " + wallet.getId(), e);
        }
    }
    @Override
    public void delete(long id) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement stmt = connection.prepareStatement(DELETE_SQL)) {
            stmt.setLong(1, id);
            int deleted = stmt.executeUpdate();
            logger.debug("Wallet deleted: id={}, rows affected={}", id, deleted);
        } catch (SQLException e) {
            logger.error("Error deleting wallet with id {}", id, e);
            throw new DaoException("Error deleting wallet with id " + id, e);
        }
    }

    private Wallet mapRowToWallet(ResultSet resultSet) throws SQLException {
        return Wallet.builder()
                .id(resultSet.getLong("id"))
                .name(resultSet.getString("name"))
                .ownerId(resultSet.getLong("owner_id"))
                .build();
    }

    @Override
    public boolean isSharedWith(long walletId, long userId) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(IS_SHARED_WITH_SQL)) {
            statement.setLong(1, walletId);
            statement.setLong(2, userId);
            try (ResultSet rs = statement.executeQuery()) {
                boolean isShared = rs.next();
                logger.debug("Wallet {} shared with user {}: {}", walletId, userId, isShared);
                return isShared;
            }
        } catch (SQLException e) {
            logger.error("Error checking wallet share for wallet {}", walletId, e);
            throw new DaoException("Error checking wallet share for wallet " + walletId, e);
        }
    }
    @Override
    public Optional<Long> findOwnerId(long walletId) {
        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_OWNER_ID_SQL)) {
            statement.setLong(1, walletId);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    Long ownerId = rs.getLong("owner_id");
                    logger.debug("Owner found for wallet {}: {}", walletId, ownerId);
                    return Optional.of(ownerId);
                }
            }
        } catch (SQLException e) {
            logger.error("Error finding owner for wallet {}", walletId, e);
            throw new DaoException("Error finding owner for wallet " + walletId, e);
        }
        return Optional.empty();
    }
}
