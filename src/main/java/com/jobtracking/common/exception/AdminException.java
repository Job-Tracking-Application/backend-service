package com.jobtracking.common.exception;

/**
 * Exception thrown for admin operation errors
 */
public class AdminException extends BusinessException {

    public AdminException(String message) {
        super(message);
    }

    public AdminException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    protected String getDefaultErrorCode() {
        return "ADMIN_OPERATION_ERROR";
    }
}