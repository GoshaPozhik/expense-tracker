package ru.itis.expensetracker.repository;

import ru.itis.expensetracker.model.Category;
import java.util.List;
import java.util.Optional;

public interface CategoryRepository {
    Category save(Category category);
    Optional<Category> findById(long id);
    List<Category> findAvailableForUser(long userId);
    void update(Category category);
    void delete(long id);
    List<Category> findAll();
}