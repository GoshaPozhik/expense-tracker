package ru.itis.expensetracker.service;

import ru.itis.expensetracker.exception.ServiceException;
import ru.itis.expensetracker.model.Category;

import java.util.List;

public interface CategoryService {
    List<Category> getAllCategoriesForUser(long userId);
    Category getCategoryById(long categoryId, long userId) throws ServiceException;
    Category createCategory(String name, long userId) throws ServiceException;
    void updateCategory(long categoryId, String name, long userId) throws ServiceException;
    void deleteCategory(long categoryId, long userId) throws ServiceException;
    boolean canModifyCategory(long categoryId, long userId);
}

