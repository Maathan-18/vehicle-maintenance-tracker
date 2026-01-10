package com.luminar.tracker.exception;

/**
 * UserNotFoundException
 * Custom exception thrown when a user is not found.
 */
public class UserNotFoundException extends RuntimeException {

    private Long userId;

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(Long userId) {
        super("User not found with ID: " + userId);
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
