package ru.itis.expensetracker.dao;

import ru.itis.expensetracker.model.Wallet;
import java.util.List;
import java.util.Optional;

public interface WalletDao {
    Wallet save(Wallet wallet);
    Optional<Wallet> findById(long id);
    List<Wallet> findAllByUserId(long userId);
    void addUserToWallet(long userId, long walletId);
    void update(Wallet wallet);
    void delete(long id);
    boolean isSharedWith(long walletId, long userId);
    Optional<Long> findOwnerId(long walletId);
}
