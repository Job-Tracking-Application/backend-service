package com.jobtracking.common.exception;

/**
 * Exception thrown when attempting to create a duplicate entity
 */
public class DuplicateEntityException extends BusinessException {

    public DuplicateEntityException(String entityType, String identifier) {
        super(entityType + " already exists: " + identifier);
    }

    public DuplicateEntityException(String message) {
        super(message);
    }

    @Override
    protected String getDefaultErrorCode() {
        return "DUPLICATE_ENTITY";
    }
}