package com.luminar.tracker.repository;

import com.luminar.tracker.entity.Reminder;
import com.luminar.tracker.entity.ReminderType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * ReminderRepository
 * Spring Data JPA repository for Reminder entity.
 * 
 * Demonstrates:
 * - Custom queries for reminder management
 * - Boolean field queries
 */
@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {

    /**
     * Find all reminders for a user (ordered by due date)
     */
    List<Reminder> findByUserIdOrderByDueDateAsc(Long userId);

    /**
     * Find unread reminders for a user
     */
    List<Reminder> findByUserIdAndIsReadFalseOrderByDueDateAsc(Long userId);

    /**
     * Find reminders for a specific vehicle
     */
    List<Reminder> findByVehicleId(Long vehicleId);

    /**
     * Count unread reminders for a user
     */
    long countByUserIdAndIsReadFalse(Long userId);

    /**
     * Find active reminders (not dismissed, not read)
     */
    @Query("SELECT r FROM Reminder r WHERE r.user.id = :userId AND r.isRead = false AND r.dismissedAt IS NULL")
    List<Reminder> findActiveReminders(@Param("userId") Long userId);

    /**
     * Check if a reminder already exists for a vehicle and type
     */
    boolean existsByVehicleIdAndReminderTypeAndIsReadFalse(Long vehicleId, ReminderType reminderType);

    /**
     * Find overdue reminders
     */
    @Query("SELECT r FROM Reminder r WHERE r.dueDate < :today AND r.isRead = false")
    List<Reminder> findOverdueReminders(@Param("today") LocalDate today);

    /**
     * Delete all reminders for a vehicle
     */
    void deleteByVehicleId(Long vehicleId);
}
