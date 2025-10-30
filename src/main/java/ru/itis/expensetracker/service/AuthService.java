package ru.itis.expensetracker.service;

import ru.itis.expensetracker.exception.ServiceException;
import ru.itis.expensetracker.model.User;

import java.util.Optional;

public interface AuthService {
    /**
     * Регистрирует нового пользователя.
     * Создает для него первый личный кошелек.
     *
     * @param username Имя пользователя.
     * @param email Email.
     * @param password Пароль в открытом виде.
     * @return Созданный объект User.
     * @throws ServiceException если email уже занят или произошла ошибка сохранения.
     */
    User register(String username, String email, String password) throws ServiceException;

    /**
     * Осуществляет вход пользователя в систему.
     *
     * @param email Email пользователя.
     * @param password Пароль в открытом виде.
     * @return Optional с объектом User в случае успеха, иначе Optional.empty().
     */
    Optional<User> login(String email, String password);
}