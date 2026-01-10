package com.luminar.tracker.service;

import com.luminar.tracker.dto.VehicleDTO;
import com.luminar.tracker.entity.ServiceRecord;
import com.luminar.tracker.entity.User;
import com.luminar.tracker.entity.Vehicle;
import com.luminar.tracker.exception.DuplicateResourceException;
import com.luminar.tracker.exception.VehicleNotFoundException;
import com.luminar.tracker.repository.ServiceRecordRepository;
import com.luminar.tracker.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * VehicleService
 * Service class for vehicle-related business logic.
 * 
 * Demonstrates:
 * - Java Streams API (JDK 8 feature) for cost calculation
 * - Lambda Expressions for filtering and mapping
 * - Method References for cleaner code
 * - Optional class for null handling
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class VehicleService {

    private final VehicleRepository vehicleRepository;
    private final ServiceRecordRepository serviceRecordRepository;
    private final UserService userService;

    /**
     * Create a new vehicle
     */
    @Transactional
    public Vehicle createVehicle(Long userId, VehicleDTO vehicleDTO) {
        log.info("Creating vehicle for user ID: {}", userId);

        User user = userService.findById(userId);

        // Check if registration number already exists
        if (vehicleRepository.findByRegNumber(vehicleDTO.getRegNumber()).isPresent()) {
            throw new DuplicateResourceException("Vehicle", "registration number", vehicleDTO.getRegNumber());
        }

        Vehicle vehicle = Vehicle.builder()
                .user(user)
                .regNumber(vehicleDTO.getRegNumber().toUpperCase())
                .make(vehicleDTO.getMake())
                .model(vehicleDTO.getModel())
                .variant(vehicleDTO.getVariant())
                .year(vehicleDTO.getYear())
                .engineCapacity(vehicleDTO.getEngineCapacity())
                .fuelType(vehicleDTO.getFuelType())
                .purchaseDate(vehicleDTO.getPurchaseDate())
                .currentOdometer(vehicleDTO.getCurrentOdometer() != null ? vehicleDTO.getCurrentOdometer() : 0)
                .nextServiceDate(vehicleDTO.getNextServiceDate())
                .nextServiceOdometer(vehicleDTO.getNextServiceOdometer())
                .notes(vehicleDTO.getNotes())
                .build();

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        log.info("Vehicle created with ID: {}", savedVehicle.getId());

        return savedVehicle;
    }

    /**
     * Get all vehicles for a user
     */
    public List<Vehicle> getVehiclesByUserId(Long userId) {
        return vehicleRepository.findByUserId(userId);
    }

    /**
     * Get vehicle by ID (with ownership check)
     */
    public Vehicle getVehicleById(Long vehicleId) {
        return vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new VehicleNotFoundException(vehicleId));
    }

    /**
     * Get vehicle by ID with user ownership validation
     */
    public Vehicle getVehicleByIdAndUserId(Long vehicleId, Long userId) {
        Vehicle vehicle = getVehicleById(vehicleId);
        if (!vehicle.getUser().getId().equals(userId)) {
            throw new VehicleNotFoundException("Vehicle not found or access denied");
        }
        return vehicle;
    }

    /**
     * Update vehicle
     */
    @Transactional
    public Vehicle updateVehicle(Long vehicleId, Long userId, VehicleDTO vehicleDTO) {
        Vehicle vehicle = getVehicleByIdAndUserId(vehicleId, userId);

        // Update editable fields
        vehicle.setCurrentOdometer(vehicleDTO.getCurrentOdometer());
        vehicle.setNextServiceDate(vehicleDTO.getNextServiceDate());
        vehicle.setNextServiceOdometer(vehicleDTO.getNextServiceOdometer());
        vehicle.setNotes(vehicleDTO.getNotes());

        // Only update variant if provided
        if (vehicleDTO.getVariant() != null) {
            vehicle.setVariant(vehicleDTO.getVariant());
        }

        return vehicleRepository.save(vehicle);
    }

    /**
     * Update vehicle odometer
     */
    @Transactional
    public Vehicle updateOdometer(Long vehicleId, Long userId, Integer newOdometer) {
        Vehicle vehicle = getVehicleByIdAndUserId(vehicleId, userId);
        
        if (newOdometer < vehicle.getCurrentOdometer()) {
            log.warn("New odometer {} is less than current odometer {}", 
                    newOdometer, vehicle.getCurrentOdometer());
        }

        vehicle.setCurrentOdometer(newOdometer);
        return vehicleRepository.save(vehicle);
    }

    /**
     * Delete vehicle
     */
    @Transactional
    public void deleteVehicle(Long vehicleId, Long userId) {
        Vehicle vehicle = getVehicleByIdAndUserId(vehicleId, userId);
        vehicleRepository.delete(vehicle);
        log.info("Vehicle deleted with ID: {}", vehicleId);
    }

    // ==========================================
    // JAVA STREAMS API DEMONSTRATIONS
    // (Per Syllabus Requirement - JDK 8 Features)
    // ==========================================

    /**
     * Calculate total maintenance cost for a vehicle
     * 
     * Demonstrates:
     * - Stream API
     * - map() for transformation
     * - reduce() for aggregation
     * - Method Reference (ServiceRecord::getCost)
     */
    public BigDecimal calculateTotalCost(Long vehicleId) {
        List<ServiceRecord> records = serviceRecordRepository.findByVehicleId(vehicleId);

        // Using Java Streams to calculate total cost
        return records.stream()
                .map(ServiceRecord::getCost)  // Method Reference - extract cost
                .reduce(BigDecimal.ZERO, BigDecimal::add);  // Sum all costs
    }

    /**
     * Calculate total maintenance cost for all vehicles of a user
     * 
     * Demonstrates:
     * - flatMap for nested stream processing
     * - Chained stream operations
     */
    public BigDecimal calculateTotalCostForUser(Long userId) {
        List<Vehicle> vehicles = vehicleRepository.findByUserId(userId);

        return vehicles.stream()
                .flatMap(vehicle -> serviceRecordRepository.findByVehicleId(vehicle.getId()).stream())
                .map(ServiceRecord::getCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculate average cost per service for a vehicle
     * 
     * Demonstrates:
     * - mapToDouble for numeric operations
     * - OptionalDouble handling
     */
    public Optional<Double> calculateAverageCost(Long vehicleId) {
        List<ServiceRecord> records = serviceRecordRepository.findByVehicleId(vehicleId);

        return records.stream()
                .mapToDouble(record -> record.getCost().doubleValue())
                .average()
                .stream()
                .boxed()
                .findFirst();
    }

    /**
     * Get vehicles sorted by total cost (most expensive first)
     * 
     * Demonstrates:
     * - sorted() with Comparator
     * - Comparator.comparing() with lambda
     * - reversed() for descending order
     */
    public List<Vehicle> getVehiclesSortedByCost(Long userId) {
        List<Vehicle> vehicles = vehicleRepository.findByUserId(userId);

        return vehicles.stream()
                .sorted(Comparator.comparing(
                        (Vehicle v) -> calculateTotalCost(v.getId()),
                        Comparator.reverseOrder()))
                .toList();  // Java 16+ feature
    }

    /**
     * Get vehicles with upcoming service (within threshold days)
     * 
     * Demonstrates:
     * - filter() with lambda predicate
     * - LocalDate comparisons
     */
    public List<Vehicle> getVehiclesWithUpcomingService(Long userId, int thresholdDays) {
        LocalDate thresholdDate = LocalDate.now().plusDays(thresholdDays);

        return vehicleRepository.findByUserId(userId).stream()
                .filter(v -> v.getNextServiceDate() != null)
                .filter(v -> v.getNextServiceDate().isBefore(thresholdDate) || 
                            v.getNextServiceDate().isEqual(thresholdDate))
                .sorted(Comparator.comparing(Vehicle::getNextServiceDate))
                .toList();
    }

    /**
     * Count vehicles by fuel type
     * 
     * Demonstrates:
     * - Collectors.groupingBy()
     * - Collectors.counting()
     */
    public java.util.Map<String, Long> countVehiclesByFuelType(Long userId) {
        return vehicleRepository.findByUserId(userId).stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        v -> v.getFuelType().getDisplayName(),
                        java.util.stream.Collectors.counting()));
    }

    /**
     * Get vehicle count for a user
     */
    public long getVehicleCount(Long userId) {
        return vehicleRepository.countByUserId(userId);
    }
}
