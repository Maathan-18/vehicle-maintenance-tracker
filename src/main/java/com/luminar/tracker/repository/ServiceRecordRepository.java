package com.luminar.tracker.repository;

import com.luminar.tracker.entity.ServiceRecord;
import com.luminar.tracker.entity.ServiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * ServiceRecordRepository
 * Spring Data JPA repository for ServiceRecord entity.
 * 
 * Demonstrates:
 * - Aggregation queries with @Query
 * - Date range queries
 * - Custom JPQL with JOIN
 */
@Repository
public interface ServiceRecordRepository extends JpaRepository<ServiceRecord, Long> {

    /**
     * Find all service records for a vehicle (ordered by date, newest first)
     */
    List<ServiceRecord> findByVehicleIdOrderByServiceDateDesc(Long vehicleId);

    /**
     * Find service records by vehicle ID
     */
    List<ServiceRecord> findByVehicleId(Long vehicleId);

    /**
     * Find service records for a user (across all vehicles)
     */
    @Query("SELECT sr FROM ServiceRecord sr WHERE sr.vehicle.user.id = :userId ORDER BY sr.serviceDate DESC")
    List<ServiceRecord> findByUserId(@Param("userId") Long userId);

    /**
     * Find service records within a date range for a user
     */
    @Query("SELECT sr FROM ServiceRecord sr WHERE sr.vehicle.user.id = :userId " +
           "AND sr.serviceDate BETWEEN :startDate AND :endDate ORDER BY sr.serviceDate DESC")
    List<ServiceRecord> findByUserIdAndDateBetween(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * Calculate total cost for a vehicle
     */
    @Query("SELECT COALESCE(SUM(sr.cost), 0) FROM ServiceRecord sr WHERE sr.vehicle.id = :vehicleId")
    BigDecimal calculateTotalCostByVehicleId(@Param("vehicleId") Long vehicleId);

    /**
     * Calculate total cost for a user (all vehicles)
     */
    @Query("SELECT COALESCE(SUM(sr.cost), 0) FROM ServiceRecord sr WHERE sr.vehicle.user.id = :userId")
    BigDecimal calculateTotalCostByUserId(@Param("userId") Long userId);

    /**
     * Get cost by service type for a user
     */
    @Query("SELECT sr.serviceType, SUM(sr.cost) FROM ServiceRecord sr " +
           "WHERE sr.vehicle.user.id = :userId GROUP BY sr.serviceType")
    List<Object[]> getCostByServiceType(@Param("userId") Long userId);

    /**
     * Get monthly costs for a user (last 12 months)
     */
    @Query("SELECT FUNCTION('DATE_FORMAT', sr.serviceDate, '%Y-%m'), SUM(sr.cost) " +
           "FROM ServiceRecord sr WHERE sr.vehicle.user.id = :userId " +
           "AND sr.serviceDate >= :startDate GROUP BY FUNCTION('DATE_FORMAT', sr.serviceDate, '%Y-%m') " +
           "ORDER BY FUNCTION('DATE_FORMAT', sr.serviceDate, '%Y-%m')")
    List<Object[]> getMonthlyCosts(@Param("userId") Long userId, @Param("startDate") LocalDate startDate);

    /**
     * Count service records for a vehicle
     */
    long countByVehicleId(Long vehicleId);

    /**
     * Find last service record for a vehicle
     */
    ServiceRecord findFirstByVehicleIdOrderByServiceDateDesc(Long vehicleId);

    /**
     * Find service records by service type
     */
    List<ServiceRecord> findByVehicleIdAndServiceType(Long vehicleId, ServiceType serviceType);
}
