package ru.itis.expensetracker.repository;

import ru.itis.expensetracker.model.User;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(long id);
}
