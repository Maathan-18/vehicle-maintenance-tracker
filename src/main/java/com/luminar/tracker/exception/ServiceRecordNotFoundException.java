package com.luminar.tracker.exception;

/**
 * ServiceRecordNotFoundException
 * Custom exception thrown when a service record is not found.
 */
public class ServiceRecordNotFoundException extends RuntimeException {

    private Long serviceRecordId;

    public ServiceRecordNotFoundException(String message) {
        super(message);
    }

    public ServiceRecordNotFoundException(Long serviceRecordId) {
        super("Service record not found with ID: " + serviceRecordId);
        this.serviceRecordId = serviceRecordId;
    }

    public Long getServiceRecordId() {
        return serviceRecordId;
    }
}
