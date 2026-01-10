package com.luminar.tracker.service;

import com.luminar.tracker.dto.DashboardDTO;
import com.luminar.tracker.entity.Vehicle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * DashboardService
 * Service class for dashboard analytics and summary data.
 * 
 * Demonstrates:
 * - Java Streams for data aggregation
 * - Collecting into nested DTOs
 * - Complex stream operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final VehicleService vehicleService;
    private final ServiceRecordService serviceRecordService;
    private final ReminderSchedulerService reminderSchedulerService;

    /**
     * Get complete dashboard data for a user
     * 
     * Demonstrates:
     * - Building complex DTO using multiple stream operations
     * - Aggregating data from multiple sources
     */
    public DashboardDTO getDashboardData(Long userId) {
        log.info("Building dashboard data for user ID: {}", userId);

        // Get basic counts
        long totalVehicles = vehicleService.getVehicleCount(userId);
        BigDecimal totalCost = serviceRecordService.getTotalCostForUser(userId);
        long unreadReminders = reminderSchedulerService.getUnreadReminderCount(userId);

        // Get upcoming services
        List<Vehicle> upcomingVehicles = vehicleService.getVehiclesWithUpcomingService(userId, 30);
        List<DashboardDTO.UpcomingServiceDTO> upcomingServices = buildUpcomingServicesList(upcomingVehicles);

        // Get monthly costs
        Map<String, BigDecimal> monthlyCosts = serviceRecordService.getMonthlyCosts(userId);

        // Get cost by category
        Map<String, BigDecimal> costByCategory = serviceRecordService.getCostByServiceType(userId);

        // Get top expensive vehicles
        List<DashboardDTO.VehicleCostSummary> topVehicles = buildTopExpensiveVehicles(userId);

        return DashboardDTO.builder()
                .totalVehicles(totalVehicles)
                .totalMaintenanceCost(totalCost)
                .upcomingServicesCount(upcomingServices.size())
                .unreadRemindersCount(unreadReminders)
                .upcomingServices(upcomingServices)
                .monthlyCosts(monthlyCosts)
                .costByCategory(costByCategory)
                .topExpensiveVehicles(topVehicles)
                .build();
    }

    /**
     * Build upcoming services list
     * 
     * Demonstrates:
     * - map() to transform Vehicle to UpcomingServiceDTO
     * - limit() to restrict results
     * - Sorting with Comparator
     */
    private List<DashboardDTO.UpcomingServiceDTO> buildUpcomingServicesList(List<Vehicle> vehicles) {
        return vehicles.stream()
                .filter(v -> v.getNextServiceDate() != null)
                .sorted(Comparator.comparing(Vehicle::getNextServiceDate))
                .limit(5)
                .map(vehicle -> DashboardDTO.UpcomingServiceDTO.builder()
                        .vehicleId(vehicle.getId())
                        .vehicleRegNumber(vehicle.getRegNumber())
                        .vehicleName(vehicle.getDisplayName())
                        .dueDate(vehicle.getNextServiceDate().toString())
                        .daysUntilDue(ChronoUnit.DAYS.between(LocalDate.now(), vehicle.getNextServiceDate()))
                        .dueOdometer(vehicle.getNextServiceOdometer())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Build top expensive vehicles list
     * 
     * Demonstrates:
     * - Complex stream with nested service calls
     * - Sorting by calculated value
     * - Limiting results
     */
    private List<DashboardDTO.VehicleCostSummary> buildTopExpensiveVehicles(Long userId) {
        List<Vehicle> vehicles = vehicleService.getVehiclesByUserId(userId);

        return vehicles.stream()
                .map(vehicle -> DashboardDTO.VehicleCostSummary.builder()
                        .vehicleId(vehicle.getId())
                        .vehicleRegNumber(vehicle.getRegNumber())
                        .vehicleName(vehicle.getDisplayName())
                        .totalCost(vehicleService.calculateTotalCost(vehicle.getId()))
                        .serviceCount(serviceRecordService.getServiceCountForVehicle(vehicle.getId()))
                        .build())
                .sorted(Comparator.comparing(DashboardDTO.VehicleCostSummary::getTotalCost).reversed())
                .limit(3)
                .collect(Collectors.toList());
    }
}
