package com.jobtracking.common.exception;

/**
 * Base exception for business logic violations
 * Provides consistent exception handling across the application
 */
public abstract class BusinessException extends RuntimeException {

    private final String errorCode;

    protected BusinessException(String message) {
        super(message);
        this.errorCode = getDefaultErrorCode();
    }

    protected BusinessException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = getDefaultErrorCode();
    }

    protected BusinessException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    protected BusinessException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Get default error code for this exception type
     * Subclasses should override this method
     */
    protected abstract String getDefaultErrorCode();
}