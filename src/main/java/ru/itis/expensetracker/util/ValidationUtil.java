package ru.itis.expensetracker.util;

import java.util.regex.Pattern;

public class ValidationUtil {
    // Паттерн для валидации email
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$"
    );
    
    // Минимальная длина пароля
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MAX_PASSWORD_LENGTH = 100;
    
    // Минимальная и максимальная длина имени пользователя
    private static final int MIN_USERNAME_LENGTH = 3;
    private static final int MAX_USERNAME_LENGTH = 50;

    /**
     * Валидирует email адрес
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Валидирует пароль (длина и не пустой)
     */
    public static boolean isValidPassword(String password) {
        if (password == null || password.trim().isEmpty()) {
            return false;
        }
        int length = password.length();
        return length >= MIN_PASSWORD_LENGTH && length <= MAX_PASSWORD_LENGTH;
    }

    /**
     * Валидирует имя пользователя
     */
    public static boolean isValidUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return false;
        }
        String trimmed = username.trim();
        int length = trimmed.length();
        return length >= MIN_USERNAME_LENGTH && length <= MAX_USERNAME_LENGTH;
    }

    /**
     * Валидирует сумму расхода (должна быть положительной)
     */
    public static boolean isValidAmount(String amountStr) {
        if (amountStr == null || amountStr.trim().isEmpty()) {
            return false;
        }
        try {
            double amount = Double.parseDouble(amountStr.trim());
            return amount > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}

