package com.luminar.tracker.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * DashboardDTO
 * Data Transfer Object for dashboard summary data.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDTO {

    private long totalVehicles;
    private BigDecimal totalMaintenanceCost;
    private long upcomingServicesCount;
    private long unreadRemindersCount;

    // Monthly costs for chart (month -> cost)
    private Map<String, BigDecimal> monthlyCosts;

    // Cost by service type (serviceType -> cost)
    private Map<String, BigDecimal> costByCategory;

    // List of upcoming services
    private List<UpcomingServiceDTO> upcomingServices;

    // Top expensive vehicles
    private List<VehicleCostSummary> topExpensiveVehicles;

    /**
     * Upcoming Service DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UpcomingServiceDTO {
        private Long vehicleId;
        private String vehicleRegNumber;
        private String vehicleName;
        private String dueDate;
        private long daysUntilDue;
        private Integer dueOdometer;
    }

    /**
     * Vehicle Cost Summary DTO
     */
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class VehicleCostSummary {
        private Long vehicleId;
        private String vehicleRegNumber;
        private String vehicleName;
        private BigDecimal totalCost;
        private long serviceCount;
    }
}
