package com.luminar.tracker.entity;

/**
 * FuelType Enum
 * Represents the fuel type of a vehicle.
 * 
 * Demonstrates:
 * - Java Enum (JDK 5+ feature)
 * - Used with JPA @Enumerated annotation
 */
public enum FuelType {
    PETROL("Petrol"),
    DIESEL("Diesel"),
    CNG("CNG"),
    ELECTRIC("Electric");

    private final String displayName;

    FuelType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
