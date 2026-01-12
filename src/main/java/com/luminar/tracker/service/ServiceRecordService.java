package com.luminar.tracker.service;

import com.luminar.tracker.dto.ServiceRecordDTO;
import com.luminar.tracker.entity.ServiceRecord;
import com.luminar.tracker.entity.ServiceType;
import com.luminar.tracker.entity.Vehicle;
import com.luminar.tracker.exception.ServiceRecordNotFoundException;
import com.luminar.tracker.repository.ServiceRecordRepository;
import com.luminar.tracker.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ServiceRecordService
 * Service class for service record (maintenance) business logic.
 * 
 * Demonstrates:
 * - Java Streams for cost aggregation by category
 * - Lambda expressions for data transformation
 * - Collectors for grouping operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ServiceRecordService {

    private final ServiceRecordRepository serviceRecordRepository;
    private final VehicleRepository vehicleRepository;
    private final VehicleService vehicleService;

    /**
     * Create a new service record
     */
    @Transactional
    public ServiceRecord createServiceRecord(Long userId, ServiceRecordDTO dto) {
        log.info("Creating service record for vehicle ID: {}", dto.getVehicleId());

        Vehicle vehicle = vehicleService.getVehicleByIdAndUserId(dto.getVehicleId(), userId);

        ServiceRecord record = ServiceRecord.builder()
                .vehicle(vehicle)
                .serviceDate(dto.getServiceDate())
                .odometerReading(dto.getOdometerReading())
                .serviceType(dto.getServiceType())
                .description(dto.getDescription())
                .cost(dto.getCost())
                .serviceCenterId(dto.getServiceCenterId())
                .serviceCenterName(dto.getServiceCenterName())
                .nextServiceDate(dto.getNextServiceDate())
                .nextServiceOdometer(dto.getNextServiceOdometer())
                .paymentMethod(dto.getPaymentMethod())
                .build();

        ServiceRecord savedRecord = serviceRecordRepository.save(record);

        // Update vehicle odometer and next service info
        updateVehicleAfterService(vehicle, dto);

        log.info("Service record created with ID: {}", savedRecord.getId());
        return savedRecord;
    }

    /**
     * Update vehicle after service is logged
     */
    private void updateVehicleAfterService(Vehicle vehicle, ServiceRecordDTO dto) {
        // Update current odometer
        vehicle.setCurrentOdometer(dto.getOdometerReading());

        // Update next service date if provided, or calculate automatically
        if (dto.getNextServiceDate() != null) {
            vehicle.setNextServiceDate(dto.getNextServiceDate());
        } else {
            // Default: next service in 6 months
            vehicle.setNextServiceDate(dto.getServiceDate().plusMonths(6));
        }

        // Update next service odometer if provided, or calculate automatically
        if (dto.getNextServiceOdometer() != null) {
            vehicle.setNextServiceOdometer(dto.getNextServiceOdometer());
        } else {
            // Default: next service in 3000 km
            vehicle.setNextServiceOdometer(dto.getOdometerReading() + 3000);
        }

        vehicleRepository.save(vehicle);
    }

    /**
     * Get all service records for a vehicle
     */
    public List<ServiceRecord> getServiceRecordsByVehicleId(Long vehicleId) {
        return serviceRecordRepository.findByVehicleIdOrderByServiceDateDesc(vehicleId);
    }

    /**
     * Get all service records for a user (across all vehicles)
     */
    public List<ServiceRecord> getServiceRecordsByUserId(Long userId) {
        return serviceRecordRepository.findByUserId(userId);
    }

    /**
     * Get service record by ID
     */
    public ServiceRecord getServiceRecordById(Long serviceRecordId) {
        return serviceRecordRepository.findById(serviceRecordId)
                .orElseThrow(() -> new ServiceRecordNotFoundException(serviceRecordId));
    }

    /**
     * Update service record
     */
    @Transactional
    public ServiceRecord updateServiceRecord(Long serviceRecordId, Long userId, ServiceRecordDTO dto) {
        ServiceRecord record = getServiceRecordById(serviceRecordId);

        // Verify ownership
        if (!record.getVehicle().getUser().getId().equals(userId)) {
            throw new ServiceRecordNotFoundException("Service record not found or access denied");
        }

        record.setServiceDate(dto.getServiceDate());
        record.setOdometerReading(dto.getOdometerReading());
        record.setServiceType(dto.getServiceType());
        record.setDescription(dto.getDescription());
        record.setCost(dto.getCost());
        record.setServiceCenterName(dto.getServiceCenterName());
        record.setNextServiceDate(dto.getNextServiceDate());
        record.setNextServiceOdometer(dto.getNextServiceOdometer());
        record.setPaymentMethod(dto.getPaymentMethod());

        return serviceRecordRepository.save(record);
    }

    /**
     * Delete service record
     */
    @Transactional
    public void deleteServiceRecord(Long serviceRecordId, Long userId) {
        ServiceRecord record = getServiceRecordById(serviceRecordId);

        // Verify ownership
        if (!record.getVehicle().getUser().getId().equals(userId)) {
            throw new ServiceRecordNotFoundException("Service record not found or access denied");
        }

        serviceRecordRepository.delete(record);
        log.info("Service record deleted with ID: {}", serviceRecordId);
    }

    // ==========================================
    // JAVA STREAMS API FOR ANALYTICS
    // ==========================================

    /**
     * Get cost breakdown by service type for a user
     * 
     * Demonstrates:
     * - Collectors.groupingBy()
     * - Collectors.reducing() for sum
     * - Lambda expressions
     */
    public Map<String, BigDecimal> getCostByServiceType(Long userId) {
        return serviceRecordRepository.findByUserId(userId).stream()
                .collect(Collectors.groupingBy(
                        record -> record.getServiceType().getDisplayName(),
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                ServiceRecord::getCost,
                                BigDecimal::add)));
    }

    /**
     * Get monthly costs for last 12 months
     * 
     * Demonstrates:
     * - Date formatting in stream
     * - TreeMap for sorted output
     */
    public Map<String, BigDecimal> getMonthlyCosts(Long userId) {
        LocalDate startDate = LocalDate.now().minusMonths(12);

        return serviceRecordRepository.findByUserIdAndDateBetween(userId, startDate, LocalDate.now())
                .stream()
                .collect(Collectors.groupingBy(
                        record -> record.getServiceDate().getYear() + "-"
                                + String.format("%02d", record.getServiceDate().getMonthValue()),
                        java.util.TreeMap::new,
                        Collectors.reducing(
                                BigDecimal.ZERO,
                                ServiceRecord::getCost,
                                BigDecimal::add)));
    }

    /**
     * Get total cost for user
     */
    public BigDecimal getTotalCostForUser(Long userId) {
        return serviceRecordRepository.calculateTotalCostByUserId(userId);
    }

    /**
     * Get total cost for vehicle
     */
    public BigDecimal getTotalCostForVehicle(Long vehicleId) {
        return serviceRecordRepository.calculateTotalCostByVehicleId(vehicleId);
    }

    /**
     * Get service count for vehicle
     */
    public long getServiceCountForVehicle(Long vehicleId) {
        return serviceRecordRepository.countByVehicleId(vehicleId);
    }

    /**
     * Get last service record for a vehicle
     */
    public ServiceRecord getLastServiceRecord(Long vehicleId) {
        return serviceRecordRepository.findFirstByVehicleIdOrderByServiceDateDesc(vehicleId);
    }
}
