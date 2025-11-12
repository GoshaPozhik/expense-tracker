package ru.itis.expensetracker.util;

import javax.servlet.http.HttpSession;
import java.security.SecureRandom;
import java.util.Base64;

public class CsrfTokenUtil {
    private static final String CSRF_TOKEN_ATTRIBUTE = "csrfToken";
    private static final SecureRandom random = new SecureRandom();
    private static final int TOKEN_LENGTH = 32;

    public static String generateToken(HttpSession session) {
        byte[] tokenBytes = new byte[TOKEN_LENGTH];
        random.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        session.setAttribute(CSRF_TOKEN_ATTRIBUTE, token);
        return token;
    }

    public static String getToken(HttpSession session) {
        String token = (String) session.getAttribute(CSRF_TOKEN_ATTRIBUTE);
        if (token == null) {
            token = generateToken(session);
        }
        return token;
    }

    public static boolean isValidToken(HttpSession session, String submittedToken) {
        if (session == null || submittedToken == null || submittedToken.trim().isEmpty()) {
            return false;
        }
        String sessionToken = (String) session.getAttribute(CSRF_TOKEN_ATTRIBUTE);
        return sessionToken != null && sessionToken.equals(submittedToken);
    }

    public static void invalidateToken(HttpSession session) {
        if (session != null) {
            session.removeAttribute(CSRF_TOKEN_ATTRIBUTE);
        }
    }
}

