package ru.itis.expensetracker.dao;


import ru.itis.expensetracker.model.Wallet;
import java.util.List;
import java.util.Optional;
public interface WalletDao {
    // Сохраняет кошелек и сразу добавляет владельца как участника
    Wallet save(Wallet wallet);
    Optional<Wallet> findById(long id);
    // Найти все кошельки, к которым у пользователя есть доступ
    List<Wallet> findAllByUserId(long userId);

    // Поделиться кошельком с другим пользователем
    void addUserToWallet(long userId, long walletId);
    void update(Wallet wallet);
    void delete(long id);

    /**
     * Проверяет, предоставлен ли доступ к кошельку для указанного пользователя.
     * @param walletId ID кошелька
     * @param userId ID пользователя
     * @return true, если доступ есть, иначе false
     */
    boolean isSharedWith(long walletId, long userId);
    /**
     * Находит ID владельца кошелька.
     * @param walletId ID кошелька
     * @return Optional с ID владельца или пустой Optional, если кошелек не найден.
     */
    Optional<Long> findOwnerId(long walletId);
}
