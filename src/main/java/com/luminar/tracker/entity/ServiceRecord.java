package com.luminar.tracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * ServiceRecord Entity
 * Represents a maintenance/service record for a vehicle.
 * 
 * Demonstrates:
 * - Many-to-One relationship (ServiceRecord belongs to Vehicle)
 * - @Enumerated for ServiceType
 * - BigDecimal for monetary values (Cost)
 */
@Entity
@Table(name = "service_records", indexes = {
        @Index(name = "idx_service_vehicle", columnList = "vehicle_id"),
        @Index(name = "idx_service_date", columnList = "service_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many-to-One: Many service records belong to one vehicle
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    @NotNull(message = "Service date is required")
    @Column(name = "service_date", nullable = false)
    private LocalDate serviceDate;

    @NotNull(message = "Odometer reading is required")
    @Min(value = 0, message = "Odometer reading cannot be negative")
    @Column(name = "odometer_reading", nullable = false)
    private Integer odometerReading;

    @NotNull(message = "Service type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false, length = 30)
    private ServiceType serviceType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Cost is required")
    @DecimalMin(value = "0.0", message = "Cost cannot be negative")
    @Digits(integer = 8, fraction = 2, message = "Invalid cost format")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal cost;

    @Column(name = "service_center_id")
    private Long serviceCenterId;

    @Column(name = "service_center_name", length = 100)
    private String serviceCenterName;

    @Column(name = "next_service_date")
    private LocalDate nextServiceDate;

    @Column(name = "next_service_odometer")
    private Integer nextServiceOdometer;

    @Size(max = 500, message = "Receipt path cannot exceed 500 characters")
    @Column(name = "receipt_path", length = 500)
    private String receiptPath;

    @Column(name = "payment_method", length = 20)
    private String paymentMethod; // CASH, CARD, UPI, NET_BANKING

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Get formatted cost with currency symbol
    public String getFormattedCost() {
        return "₹" + cost.toString();
    }
}
