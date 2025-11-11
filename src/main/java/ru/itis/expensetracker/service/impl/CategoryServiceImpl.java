package ru.itis.expensetracker.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.itis.expensetracker.dao.CategoryDao;
import ru.itis.expensetracker.exception.DaoException;
import ru.itis.expensetracker.exception.ServiceException;
import ru.itis.expensetracker.model.Category;
import ru.itis.expensetracker.service.CategoryService;

import java.util.List;
import java.util.Optional;

public class CategoryServiceImpl implements CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    private final CategoryDao categoryDao;

    public CategoryServiceImpl(CategoryDao categoryDao) {
        this.categoryDao = categoryDao;
    }

    @Override
    public List<Category> getAllCategoriesForUser(long userId) {
        return categoryDao.findAvailableForUser(userId);
    }

    @Override
    public Category getCategoryById(long categoryId, long userId) throws ServiceException {
        Category category = categoryDao.findById(categoryId)
                .orElseThrow(() -> new ServiceException("Категория с ID " + categoryId + " не найдена."));

        // Проверяем, что пользователь может видеть эту категорию
        if (category.getUserId() != null && !category.getUserId().equals(userId)) {
            throw new ServiceException("Доступ к категории запрещен.");
        }

        return category;
    }

    @Override
    public Category createCategory(String name, long userId) throws ServiceException {
        if (name == null || name.trim().isEmpty()) {
            throw new ServiceException("Название категории не может быть пустым.");
        }

        String trimmedName = name.trim();
        if (trimmedName.length() > 100) {
            throw new ServiceException("Название категории не может превышать 100 символов.");
        }

        Category category = Category.builder()
                .name(trimmedName)
                .userId(userId)
                .build();

        try {
            Category saved = categoryDao.save(category);
            logger.info("Category created: {} by user: {}", trimmedName, userId);
            return saved;
        } catch (DaoException e) {
            logger.error("Error creating category: {}", trimmedName, e);
            throw new ServiceException("Не удалось создать категорию.", e);
        }
    }

    @Override
    public void updateCategory(long categoryId, String name, long userId) throws ServiceException {
        Category category = categoryDao.findById(categoryId)
                .orElseThrow(() -> new ServiceException("Категория с ID " + categoryId + " не найдена."));

        // Глобальные категории (user_id = NULL) нельзя редактировать
        if (category.getUserId() == null) {
            throw new ServiceException("Глобальные категории нельзя редактировать.");
        }

        // Проверяем права доступа
        if (!category.getUserId().equals(userId)) {
            throw new ServiceException("Вы не можете редактировать эту категорию.");
        }

        if (name == null || name.trim().isEmpty()) {
            throw new ServiceException("Название категории не может быть пустым.");
        }

        String trimmedName = name.trim();
        if (trimmedName.length() > 100) {
            throw new ServiceException("Название категории не может превышать 100 символов.");
        }

        category.setName(trimmedName);

        try {
            categoryDao.update(category);
            logger.info("Category updated: {} by user: {}", categoryId, userId);
        } catch (DaoException e) {
            logger.error("Error updating category: {}", categoryId, e);
            throw new ServiceException("Не удалось обновить категорию.", e);
        }
    }

    @Override
    public void deleteCategory(long categoryId, long userId) throws ServiceException {
        Category category = categoryDao.findById(categoryId)
                .orElseThrow(() -> new ServiceException("Категория с ID " + categoryId + " не найдена."));

        // Глобальные категории нельзя удалять
        if (category.getUserId() == null) {
            throw new ServiceException("Глобальные категории нельзя удалять.");
        }

        // Проверяем права доступа
        if (!category.getUserId().equals(userId)) {
            throw new ServiceException("Вы не можете удалить эту категорию.");
        }

        try {
            categoryDao.delete(categoryId);
            logger.info("Category deleted: {} by user: {}", categoryId, userId);
        } catch (DaoException e) {
            logger.error("Error deleting category: {}", categoryId, e);
            throw new ServiceException("Не удалось удалить категорию.", e);
        }
    }

    @Override
    public boolean canModifyCategory(long categoryId, long userId) {
        Optional<Category> categoryOpt = categoryDao.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            return false;
        }

        Category category = categoryOpt.get();
        // Глобальные категории нельзя модифицировать
        if (category.getUserId() == null) {
            return false;
        }

        // Только владелец может модифицировать
        return category.getUserId().equals(userId);
    }
}

