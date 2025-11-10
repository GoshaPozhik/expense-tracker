package ru.itis.expensetracker.service.impl;

import ru.itis.expensetracker.dao.UserDao;
import ru.itis.expensetracker.dao.WalletDao;
import ru.itis.expensetracker.exception.DaoException;
import ru.itis.expensetracker.exception.ServiceException;
import ru.itis.expensetracker.model.User;
import ru.itis.expensetracker.model.Wallet;
import ru.itis.expensetracker.service.AuthService;
import ru.itis.expensetracker.util.PasswordHasher;
import java.util.Optional;

public class AuthServiceImpl implements AuthService {

    private final UserDao userDao;
    private final WalletDao walletDao;

    public AuthServiceImpl(UserDao userDao, WalletDao walletDao) {
        this.userDao = userDao;
        this.walletDao = walletDao;
    }

    @Override
    public User register(String username, String email, String password, String confirmPassword) throws ServiceException {
        if (userDao.findByEmail(email).isPresent()) {
            throw new ServiceException("Пользователь с email '" + email + "' уже существует.");
        }
        if (!password.equals(confirmPassword)) {
            throw new ServiceException("Пароли не совпадают.");
        }

        String passwordHash = PasswordHasher.hashPassword(password);

        User userToSave = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordHash)
                .build();

        try {
            User savedUser = userDao.save(userToSave);

            Wallet personalWallet = Wallet.builder()
                    .name("Личный")
                    .ownerId(savedUser.getId())
                    .build();
            walletDao.save(personalWallet);

            return savedUser;

        } catch (DaoException e) {
            throw new ServiceException("Не удалось зарегистрировать пользователя.", e);
        }
    }

    @Override
    public Optional<User> login(String email, String password) {
        Optional<User> userOptional = userDao.findByEmail(email);
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();

        if (PasswordHasher.checkPassword(password, user.getPasswordHash())) {
            return Optional.of(user);
        }

        return Optional.empty();
    }
}