package com.luminar.tracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Vehicle Entity
 * Represents a vehicle registered by a user for maintenance tracking.
 * 
 * Demonstrates:
 * - Many-to-One relationship (Vehicle belongs to User)
 * - One-to-Many relationship (Vehicle has many ServiceRecords)
 * - @Enumerated for FuelType
 * - Indexing for frequently queried columns
 */
@Entity
@Table(name = "vehicles", indexes = {
    @Index(name = "idx_vehicle_user", columnList = "user_id"),
    @Index(name = "idx_vehicle_reg_number", columnList = "reg_number")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many-to-One: Many vehicles belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotBlank(message = "Registration number is required")
    @Pattern(regexp = "^[A-Z]{2}[0-9]{1,2}[A-Z]{1,3}[0-9]{4}$", 
             message = "Invalid registration number format (e.g., KL01AB1234)")
    @Column(name = "reg_number", unique = true, nullable = false, length = 20)
    private String regNumber;

    @NotBlank(message = "Make is required")
    @Size(max = 50, message = "Make cannot exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String make;

    @NotBlank(message = "Model is required")
    @Size(max = 50, message = "Model cannot exceed 50 characters")
    @Column(nullable = false, length = 50)
    private String model;

    @Size(max = 50, message = "Variant cannot exceed 50 characters")
    @Column(length = 50)
    private String variant;

    @NotNull(message = "Year is required")
    @Min(value = 1990, message = "Year must be 1990 or later")
    @Max(value = 2100, message = "Year must be realistic")
    @Column(nullable = false)
    private Integer year;

    @Min(value = 50, message = "Engine capacity must be at least 50cc")
    @Max(value = 10000, message = "Engine capacity cannot exceed 10000cc")
    @Column(name = "engine_capacity")
    private Integer engineCapacity;

    @NotNull(message = "Fuel type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "fuel_type", nullable = false, length = 20)
    private FuelType fuelType;

    @Column(name = "purchase_date")
    private LocalDate purchaseDate;

    @Min(value = 0, message = "Odometer cannot be negative")
    @Column(name = "current_odometer")
    @Builder.Default
    private Integer currentOdometer = 0;

    @Column(name = "next_service_date")
    private LocalDate nextServiceDate;

    @Column(name = "next_service_odometer")
    private Integer nextServiceOdometer;

    @Size(max = 500, message = "RC path cannot exceed 500 characters")
    @Column(name = "rc_path", length = 500)
    private String rcPath;

    @Column(columnDefinition = "TEXT")
    private String notes;

    // One vehicle has many service records
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ServiceRecord> serviceRecords = new ArrayList<>();

    // One vehicle has many reminders
    @OneToMany(mappedBy = "vehicle", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Reminder> reminders = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Helper method to add service record
    public void addServiceRecord(ServiceRecord serviceRecord) {
        serviceRecords.add(serviceRecord);
        serviceRecord.setVehicle(this);
    }

    // Helper method to remove service record
    public void removeServiceRecord(ServiceRecord serviceRecord) {
        serviceRecords.remove(serviceRecord);
        serviceRecord.setVehicle(null);
    }

    // Get display name (Make + Model)
    public String getDisplayName() {
        return make + " " + model + (variant != null ? " " + variant : "");
    }
}
