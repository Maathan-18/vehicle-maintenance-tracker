package com.luminar.tracker.entity;

/**
 * ServiceType Enum
 * Represents the type of service/maintenance performed on a vehicle.
 * 
 * Demonstrates:
 * - Java Enum with constructor and fields
 * - Used with JPA @Enumerated annotation
 */
public enum ServiceType {
    OIL_CHANGE("Oil Change"),
    FILTER_REPLACEMENT("Filter Replacement"),
    CHAIN_LUBE("Chain Lube"),
    TYRE_SERVICE("Tyre Service"),
    BATTERY_REPLACEMENT("Battery Replacement"),
    BRAKE_SERVICE("Brake Service"),
    SUSPENSION("Suspension"),
    GENERAL_SERVICE("General Service"),
    OTHER("Other");

    private final String displayName;

    ServiceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
