package com.jobtracking.common.utils;

import com.jobtracking.common.exception.AuthorizationException;
import com.jobtracking.common.exception.DuplicateEntityException;
import com.jobtracking.common.exception.EntityNotFoundException;
import com.jobtracking.common.exception.ValidationException;

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
            throw new ValidationException("field", message);
        }
    }

    /**
     * Validate that string is not null or empty
     */
    public static void validateNotEmpty(String str, String message) {
        if (str == null || str.trim().isEmpty()) {
            throw new ValidationException("field", message);
        }
    }

    /**
     * Validate that entity doesn't already exist (for duplicate prevention)
     */
    public static void validateNotExists(boolean exists, String message) {
        if (exists) {
            throw new DuplicateEntityException(message);
        }
    }

    /**
     * Validate that entity exists
     */
    public static void validateExists(boolean exists, String message) {
        if (!exists) {
            throw new EntityNotFoundException(message);
        }
    }

    /**
     * Validate ownership (user owns the resource)
     */
    public static void validateOwnership(Long ownerId, Long userId, String message) {
        if (!ownerId.equals(userId)) {
            throw new AuthorizationException(message);
        }
    }

    /**
     * Validate authorization (user is authorized for the operation)
     */
    public static void validateAuthorization(boolean authorized, String message) {
        if (!authorized) {
            throw new AuthorizationException(message);
        }
    }

    /**
     * Validate positive number
     */
    public static void validatePositive(Number number, String message) {
        if (number == null || number.doubleValue() <= 0) {
            throw new ValidationException("field", message);
        }
    }

    /**
     * Validate email format (basic validation)
     */
    public static void validateEmail(String email, String message) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("email", message);
        }
    }

    /**
     * Validate that value is within range
     */
    public static void validateRange(Number value, Number min, Number max, String message) {
        if (value == null || value.doubleValue() < min.doubleValue() || value.doubleValue() > max.doubleValue()) {
            throw new ValidationException("field", message);
        }
    }

    /**
     * Validate that collection is not null or empty
     */
    public static void validateNotEmpty(java.util.Collection<?> collection, String message) {
        if (collection == null || collection.isEmpty()) {
            throw new ValidationException("collection", message);
        }
    }
}