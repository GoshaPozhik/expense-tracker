package ru.itis.expensetracker.dao;

import ru.itis.expensetracker.model.User;
import java.util.Optional;

public interface UserDao {
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(long id);
}
