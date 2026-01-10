package com.luminar.tracker.exception;

/**
 * DuplicateResourceException
 * Custom exception thrown when attempting to create a duplicate resource.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    public DuplicateResourceException(String resourceType, String field, String value) {
        super(resourceType + " already exists with " + field + ": " + value);
    }
}
