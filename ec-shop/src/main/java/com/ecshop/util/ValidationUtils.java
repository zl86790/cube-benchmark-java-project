package com.ecshop.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidationUtils {

    // BUG #12 (MEDIUM): Email regex is too permissive
    // Allows: "a@b", "user@domain" (no TLD), "user@.com", "user@domain." etc.
    // Missing proper TLD validation, missing length checks
    // A proper email regex should be: ^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$
    private static final String EMAIL_REGEX = "^[^@]+@[^@]+\\.[^@]+$";

    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        boolean valid = email.matches(EMAIL_REGEX);
        log.debug("Email validation for '{}': {}", email, valid);
        return valid;
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        return phone.matches("^\\+?[0-9]{7,15}$");
    }

    public static boolean isValidPassword(String password) {
        if (password == null) {
            return false;
        }
        // At least 6 characters
        return password.length() >= 6;
    }

    public static boolean isNullOrEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
}
