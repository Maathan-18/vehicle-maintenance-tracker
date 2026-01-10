package com.luminar.tracker.exception;

/**
 * VehicleNotFoundException
 * Custom exception thrown when a vehicle is not found.
 * 
 * Demonstrates:
 * - User-defined Exception (per syllabus requirement)
 * - RuntimeException for unchecked exception handling
 */
public class VehicleNotFoundException extends RuntimeException {

    private Long vehicleId;

    public VehicleNotFoundException(String message) {
        super(message);
    }

    public VehicleNotFoundException(Long vehicleId) {
        super("Vehicle not found with ID: " + vehicleId);
        this.vehicleId = vehicleId;
    }

    public VehicleNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public Long getVehicleId() {
        return vehicleId;
    }
}
