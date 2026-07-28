package com.ecshop.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ValidationUtils {
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
        return Pattern.compile("^\\+?[0-9]{7,15}$").matcher(phone).matches();
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
