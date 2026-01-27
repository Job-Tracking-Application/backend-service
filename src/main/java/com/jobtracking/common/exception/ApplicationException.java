package com.jobtracking.common.exception;

/**
 * Exception thrown for general application processing errors
 */
public class ApplicationException extends BusinessException {

    public ApplicationException(String message) {
        super(message);
    }

    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    protected String getDefaultErrorCode() {
        return "APPLICATION_ERROR";
    }
}