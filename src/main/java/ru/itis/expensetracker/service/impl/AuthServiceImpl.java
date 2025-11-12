package ru.itis.expensetracker.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itis.expensetracker.repository.UserRepository;
import ru.itis.expensetracker.repository.WalletRepository;
import ru.itis.expensetracker.exception.RepositoryException;
import ru.itis.expensetracker.exception.ServiceException;
import ru.itis.expensetracker.model.User;
import ru.itis.expensetracker.model.Wallet;
import ru.itis.expensetracker.service.AuthService;
import ru.itis.expensetracker.util.PasswordHasher;
import ru.itis.expensetracker.util.ValidationUtil;
import java.util.Optional;

public class AuthServiceImpl implements AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public AuthServiceImpl(UserRepository userRepository, WalletRepository walletRepository) {
        this.userRepository = userRepository;
        this.walletRepository = walletRepository;
    }

    @Override
    public void register(String username, String email, String password, String confirmPassword) throws ServiceException {
        if (!ValidationUtil.isValidUsername(username)) {
            throw new ServiceException("Имя пользователя должно содержать от 3 до 50 символов.");
        }
        
        if (ValidationUtil.isValidEmail(email)) {
            throw new ServiceException("Некорректный формат email адреса.");
        }
        
        if (userRepository.findByEmail(email).isPresent()) {
            throw new ServiceException("Пользователь с email '" + email + "' уже существует.");
        }
        
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
            User savedUser = userRepository.save(userToSave);
            logger.info("User registered successfully: {}", email);

            Wallet personalWallet = Wallet.builder()
                    .name("Личный")
                    .ownerId(savedUser.getId())
                    .build();
            walletRepository.save(personalWallet);
            logger.debug("Personal wallet created for user: {}", savedUser.getId());

        } catch (RepositoryException e) {
            logger.error("Error registering user: {}", email, e);
            throw new ServiceException("Не удалось зарегистрировать пользователя.", e);
        }
    }

    @Override
    public Optional<User> login(String email, String password) {
        if (ValidationUtil.isValidEmail(email)) {
            return Optional.empty();
        }
        
        Optional<User> userOptional = userRepository.findByEmail(email);
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