package ru.itis.expensetracker.repository;

import ru.itis.expensetracker.model.Wallet;
import java.util.List;
import java.util.Optional;

public interface WalletRepository {
    Wallet save(Wallet wallet);
    Optional<Wallet> findById(long id);
    List<Wallet> findAllByUserId(long userId);
    void update(Wallet wallet);
    void delete(long id);
    void addUserToWallet(long userId, long walletId);
    boolean isSharedWith(long walletId, long userId);
    Optional<Long> findOwnerId(long walletId);
}
