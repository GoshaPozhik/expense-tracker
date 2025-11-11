package ru.itis.expensetracker.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itis.expensetracker.dao.UserDao;
import ru.itis.expensetracker.dao.WalletDao;
import ru.itis.expensetracker.exception.DaoException;
import ru.itis.expensetracker.exception.ServiceException;
import ru.itis.expensetracker.model.User;
import ru.itis.expensetracker.model.Wallet;
import ru.itis.expensetracker.service.AuthService;
import ru.itis.expensetracker.util.PasswordHasher;
import ru.itis.expensetracker.util.ValidationUtil;
import java.util.Optional;

public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserDao userDao;
    private final WalletDao walletDao;

    public AuthServiceImpl(UserDao userDao, WalletDao walletDao) {
        this.userDao = userDao;
        this.walletDao = walletDao;
    }

    @Override
    public User register(String username, String email, String password, String confirmPassword) throws ServiceException {
        // Валидация имени пользователя
        if (!ValidationUtil.isValidUsername(username)) {
            throw new ServiceException("Имя пользователя должно содержать от 3 до 50 символов.");
        }
        
        // Валидация email
        if (!ValidationUtil.isValidEmail(email)) {
            throw new ServiceException("Некорректный формат email адреса.");
        }
        
        // Проверка существования пользователя
        if (userDao.findByEmail(email).isPresent()) {
            throw new ServiceException("Пользователь с email '" + email + "' уже существует.");
        }
        
        // Валидация пароля
        if (!ValidationUtil.isValidPassword(password)) {
            throw new ServiceException("Пароль должен содержать от 6 до 100 символов.");
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
            logger.info("User registered successfully: {}", email);

            Wallet personalWallet = Wallet.builder()
                    .name("Личный")
                    .ownerId(savedUser.getId())
                    .build();
            walletDao.save(personalWallet);
            logger.debug("Personal wallet created for user: {}", savedUser.getId());

            return savedUser;

        } catch (DaoException e) {
            logger.error("Error registering user: {}", email, e);
            throw new ServiceException("Не удалось зарегистрировать пользователя.", e);
        }
    }

    @Override
    public Optional<User> login(String email, String password) {
        // Валидация email
        if (!ValidationUtil.isValidEmail(email)) {
            return Optional.empty();
        }
        
        Optional<User> userOptional = userDao.findByEmail(email);
        if (userOptional.isEmpty()) {
            return Optional.empty();
        }

        User user = userOptional.get();

        if (PasswordHasher.checkPassword(password, user.getPasswordHash())) {
            logger.info("User logged in successfully: {}", email);
            return Optional.of(user);
        }

        logger.warn("Failed login attempt for email: {}", email);
        return Optional.empty();
    }
}