package com.luminar.tracker.dto;

import com.luminar.tracker.entity.FuelType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;

/**
 * VehicleDTO
 * Data Transfer Object for Vehicle entity.
 * 
 * Demonstrates:
 * - DTO pattern for API requests/responses
 * - Bean Validation annotations
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleDTO {

    private Long id;

    @NotBlank(message = "Registration number is required")
    @Pattern(regexp = "^[A-Z]{2}[0-9]{1,2}[A-Z]{1,3}[0-9]{4}$", 
             message = "Invalid registration number format (e.g., KL01AB1234)")
    private String regNumber;

    @NotBlank(message = "Make is required")
    @Size(max = 50, message = "Make cannot exceed 50 characters")
    private String make;

    @NotBlank(message = "Model is required")
    @Size(max = 50, message = "Model cannot exceed 50 characters")
    private String model;

    private String variant;

    @NotNull(message = "Year is required")
    @Min(value = 1990, message = "Year must be 1990 or later")
    private Integer year;

    private Integer engineCapacity;

    @NotNull(message = "Fuel type is required")
    private FuelType fuelType;

    private LocalDate purchaseDate;

    @Min(value = 0, message = "Odometer cannot be negative")
    private Integer currentOdometer = 0;

    private LocalDate nextServiceDate;

    private Integer nextServiceOdometer;

    private String notes;
}
