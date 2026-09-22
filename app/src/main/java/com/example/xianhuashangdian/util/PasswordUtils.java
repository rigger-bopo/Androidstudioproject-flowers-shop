package com.example.xianhuashangdian.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public final class PasswordUtils {
    private PasswordUtils() {
    }

    public static String hashPassword(String username, String password) {
        return sha256(username.trim().toLowerCase(Locale.ROOT) + ":" + password);
    }

    public static String hashAnswer(String answer) {
        return sha256(answer.trim().toLowerCase(Locale.ROOT));
    }

    private static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] bytes = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder(bytes.length * 2);
            for (byte b : bytes) {
                result.append(String.format("%02x", b));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }
}
