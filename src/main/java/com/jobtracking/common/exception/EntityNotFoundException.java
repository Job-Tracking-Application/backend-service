package com.jobtracking.common.exception;

/**
 * Exception thrown when a requested entity is not found
 */
public class EntityNotFoundException extends BusinessException {

    public EntityNotFoundException(String entityType, Long id) {
        super(entityType + " not found with ID: " + id);
    }

    public EntityNotFoundException(String entityType, String identifier) {
        super(entityType + " not found: " + identifier);
    }

    public EntityNotFoundException(String message) {
        super(message);
    }

    @Override
    protected String getDefaultErrorCode() {
        return "ENTITY_NOT_FOUND";
    }
}