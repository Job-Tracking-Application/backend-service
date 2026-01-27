package com.jobtracking.common.exception;

import java.util.Map;

/**
 * Exception thrown when validation fails
 */
public class ValidationException extends BusinessException {

    private final Map<String, String> fieldErrors;

    public ValidationException(String message) {
        super(message);
        this.fieldErrors = Map.of();
    }

    public ValidationException(String field, String error) {
        super("Validation failed for field: " + field);
        this.fieldErrors = Map.of(field, error);
    }

    public ValidationException(Map<String, String> fieldErrors) {
        super("Validation failed for multiple fields");
        this.fieldErrors = fieldErrors;
    }

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    @Override
    protected String getDefaultErrorCode() {
        return "VALIDATION_FAILED";
    }
}