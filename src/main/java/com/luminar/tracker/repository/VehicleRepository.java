package com.luminar.tracker.repository;

import com.luminar.tracker.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * VehicleRepository
 * Spring Data JPA repository for Vehicle entity.
 * 
 * Demonstrates:
 * - Custom JPQL queries using @Query
 * - Method naming convention queries
 * - Parameterized queries
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    /**
     * Find all vehicles for a specific user
     */
    List<Vehicle> findByUserId(Long userId);

    /**
     * Find vehicle by registration number
     */
    Optional<Vehicle> findByRegNumber(String regNumber);

    /**
     * Check if registration number exists for a user
     */
    boolean existsByRegNumberAndUserId(String regNumber, Long userId);

    /**
     * Find vehicles with upcoming service due date
     */
    @Query("SELECT v FROM Vehicle v WHERE v.user.id = :userId AND v.nextServiceDate <= :thresholdDate")
    List<Vehicle> findVehiclesWithUpcomingService(
            @Param("userId") Long userId, 
            @Param("thresholdDate") LocalDate thresholdDate);

    /**
     * Find vehicles where odometer is close to next service odometer
     */
    @Query("SELECT v FROM Vehicle v WHERE v.user.id = :userId AND " +
           "(v.nextServiceOdometer - v.currentOdometer) <= :thresholdKm")
    List<Vehicle> findVehiclesNearServiceOdometer(
            @Param("userId") Long userId, 
            @Param("thresholdKm") Integer thresholdKm);

    /**
     * Count vehicles for a user
     */
    long countByUserId(Long userId);

    /**
     * Find all vehicles with service due (for scheduler)
     */
    @Query("SELECT v FROM Vehicle v WHERE v.nextServiceDate IS NOT NULL AND v.nextServiceDate <= :thresholdDate")
    List<Vehicle> findAllVehiclesWithUpcomingService(@Param("thresholdDate") LocalDate thresholdDate);
}
