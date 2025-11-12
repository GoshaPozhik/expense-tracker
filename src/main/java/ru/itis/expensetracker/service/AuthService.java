package ru.itis.expensetracker.service;

import ru.itis.expensetracker.exception.ServiceException;
import ru.itis.expensetracker.model.User;
import java.util.Optional;

public interface AuthService {
    void register(String username, String email, String password, String confirmPassword) throws ServiceException;
    Optional<User> login(String email, String password);
}