package com.jobtracking.common.exception;

/**
 * Exception thrown when user is not authorized to perform an operation
 */
public class AuthorizationException extends BusinessException {

    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException(String operation, String resource) {
        super("Not authorized to " + operation + " on " + resource);
    }

    @Override
    protected String getDefaultErrorCode() {
        return "AUTHORIZATION_FAILED";
    }
}