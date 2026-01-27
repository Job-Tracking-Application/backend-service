package com.jobtracking.common.utils;

/**
 * Utility class for common validation operations
 * Eliminates duplicate validation logic across services
 */
public class ValidationUtil {

    /**
     * Validate that object is not null
     */
    public static void validateNotNull(Object obj, String message) {
        if (obj == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Validate that string is not null or empty
     */
    public static void validateNotEmpty(String str, String message) {
        if (str == null || str.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Validate that entity doesn't already exist (for duplicate prevention)
     */
    public static void validateNotExists(boolean exists, String message) {
        if (exists) {
            throw new IllegalStateException(message);
        }
    }

    /**
     * Validate that entity exists
     */
    public static void validateExists(boolean exists, String message) {
        if (!exists) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Validate ownership (user owns the resource)
     */
    public static void validateOwnership(Long ownerId, Long userId, String message) {
        if (!ownerId.equals(userId)) {
            throw new SecurityException(message);
        }
    }

    /**
     * Validate authorization (user is authorized for the operation)
     */
    public static void validateAuthorization(boolean authorized, String message) {
        if (!authorized) {
            throw new SecurityException(message);
        }
    }

    /**
     * Validate positive number
     */
    public static void validatePositive(Number number, String message) {
        if (number == null || number.doubleValue() <= 0) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Validate email format (basic validation)
     */
    public static void validateEmail(String email, String message) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Validate that value is within range
     */
    public static void validateRange(Number value, Number min, Number max, String message) {
        if (value == null || value.doubleValue() < min.doubleValue() || value.doubleValue() > max.doubleValue()) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * Validate that collection is not null or empty
     */
    public static void validateNotEmpty(java.util.Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new IllegalArgumentException(message);
        }
    }
}