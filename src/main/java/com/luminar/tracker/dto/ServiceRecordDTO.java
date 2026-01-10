package com.luminar.tracker.dto;

import com.luminar.tracker.entity.ServiceType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * ServiceRecordDTO
 * Data Transfer Object for ServiceRecord entity.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRecordDTO {

    private Long id;

    @NotNull(message = "Vehicle ID is required")
    private Long vehicleId;

    @NotNull(message = "Service date is required")
    private LocalDate serviceDate;

    @NotNull(message = "Odometer reading is required")
    @Min(value = 0, message = "Odometer reading cannot be negative")
    private Integer odometerReading;

    @NotNull(message = "Service type is required")
    private ServiceType serviceType;

    private String description;

    @NotNull(message = "Cost is required")
    @DecimalMin(value = "0.0", message = "Cost cannot be negative")
    private BigDecimal cost;

    private Long serviceCenterId;

    private String serviceCenterName;

    private LocalDate nextServiceDate;

    private Integer nextServiceOdometer;

    // Transient fields for display
    private String vehicleRegNumber;
    private String vehicleName;
}
