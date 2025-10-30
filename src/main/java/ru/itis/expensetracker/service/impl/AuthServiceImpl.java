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

    // Внедрение зависимостей через конструктор
    public AuthServiceImpl(UserDao userDao, WalletDao walletDao) {
        this.userDao = userDao;
        this.walletDao = walletDao;
    }

    @Override
    public User register(String username, String email, String password) throws ServiceException {
        // 1. Проверка, что email не занят
        if (userDao.findByEmail(email).isPresent()) {
            throw new ServiceException("Пользователь с email '" + email + "' уже существует.");
        }

        // 2. Хеширование пароля
        String passwordHash = PasswordHasher.hashPassword(password);

        // 3. Создание объекта User
        User userToSave = User.builder()
                .username(username)
                .email(email)
                .passwordHash(passwordHash)
                .build();

        try {
            // 4. Сохранение пользователя в БД
            User savedUser = userDao.save(userToSave);

            // 5. Бизнес-логика: при регистрации автоматически создаем
            // пользователю его первый личный кошелек.
            Wallet personalWallet = Wallet.builder()
                    .name("Личный")
                    .ownerId(savedUser.getId())
                    .build();
            walletDao.save(personalWallet);

            return savedUser;

        } catch (DaoException e) {
            // Оборачиваем исключение DAO в сервисное исключение
            throw new ServiceException("Не удалось зарегистрировать пользователя.", e);
        }
    }

    @Override
    public Optional<User> login(String email, String password) {
        // 1. Находим пользователя по email
        Optional<User> userOptional = userDao.findByEmail(email);
        if (userOptional.isEmpty()) {
            return Optional.empty(); // Пользователь не найден
        }

        User user = userOptional.get();

        // 2. Проверяем пароль
        if (PasswordHasher.checkPassword(password, user.getPasswordHash())) {
            return Optional.of(user); // Успешный вход
        }

        return Optional.empty(); // Неверный пароль
    }
}