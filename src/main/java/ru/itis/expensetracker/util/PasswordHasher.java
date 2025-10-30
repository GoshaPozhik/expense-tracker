package ru.itis.expensetracker.util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

    /**
     * Хеширует пароль с использованием BCrypt.
     * @param plainTextPassword Пароль в открытом виде.
     * @return Строка с хешем.
     */
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt());
    }

    /**
     * Проверяет, соответствует ли пароль в открытом виде хешу из базы данных.
     * @param plainTextPassword Пароль, введенный пользователем.
     * @param hashedPassword Хеш из БД.
     * @return true, если пароли совпадают, иначе false.
     */
    public static boolean checkPassword(String plainTextPassword, String hashedPassword) {
        return BCrypt.checkpw(plainTextPassword, hashedPassword);
    }
}