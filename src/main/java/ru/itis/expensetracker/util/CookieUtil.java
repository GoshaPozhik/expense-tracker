package ru.itis.expensetracker.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Optional;

public class CookieUtil {
    private static final String REMEMBER_ME_COOKIE_NAME = "rememberMe";
    private static final String USER_ID_COOKIE_NAME = "userId";
    private static final int COOKIE_MAX_AGE = 30 * 24 * 60 * 60;

    public static void createRememberMeCookie(HttpServletResponse response, Long userId) {
        Cookie userIdCookie = new Cookie(USER_ID_COOKIE_NAME, String.valueOf(userId));
        userIdCookie.setMaxAge(COOKIE_MAX_AGE);
        userIdCookie.setPath("/");
        response.addCookie(userIdCookie);

        Cookie rememberMeCookie = new Cookie(REMEMBER_ME_COOKIE_NAME, "true");
        rememberMeCookie.setMaxAge(COOKIE_MAX_AGE);
        rememberMeCookie.setPath("/");
        response.addCookie(rememberMeCookie);
    }

    public static void deleteRememberMeCookies(HttpServletResponse response) {
        Cookie userIdCookie = new Cookie(USER_ID_COOKIE_NAME, "");
        userIdCookie.setMaxAge(0);
        userIdCookie.setPath("/");
        response.addCookie(userIdCookie);

        Cookie rememberMeCookie = new Cookie(REMEMBER_ME_COOKIE_NAME, "");
        rememberMeCookie.setMaxAge(0);
        rememberMeCookie.setPath("/");
        response.addCookie(rememberMeCookie);
    }

    public static Optional<String> getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst();
    }

    public static boolean hasRememberMeCookie(HttpServletRequest request) {
        return getCookieValue(request, REMEMBER_ME_COOKIE_NAME)
                .map("true"::equals)
                .orElse(false);
    }

    public static Optional<Long> getUserIdFromCookie(HttpServletRequest request) {
        return getCookieValue(request, USER_ID_COOKIE_NAME)
                .map(value -> {
                    try {
                        return Long.parseLong(value);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                });
    }
}

