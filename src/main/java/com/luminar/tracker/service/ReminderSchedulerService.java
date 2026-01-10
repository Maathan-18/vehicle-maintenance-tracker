package com.luminar.tracker.service;

import com.luminar.tracker.entity.Reminder;
import com.luminar.tracker.entity.ReminderType;
import com.luminar.tracker.entity.Vehicle;
import com.luminar.tracker.repository.ReminderRepository;
import com.luminar.tracker.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * ReminderSchedulerService
 * Service class for automated reminder generation using Spring Scheduler.
 * 
 * Demonstrates:
 * - @Scheduled annotation for cron-based scheduling (per syllabus)
 * - Spring Scheduler with @EnableScheduling
 * - Automatic reminder generation based on date/odometer thresholds
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderSchedulerService {

    private final VehicleRepository vehicleRepository;
    private final ReminderRepository reminderRepository;

    @Value("${app.reminder.threshold-days:7}")
    private int thresholdDays;

    @Value("${app.reminder.threshold-km:500}")
    private int thresholdKm;

    /**
     * Scheduled job to generate service reminders
     * Runs every day at 8:00 AM
     * 
     * Demonstrates:
     * - @Scheduled with cron expression
     * - Spring Scheduler (per syllabus: Spring Scheduler / Quartz)
     */
    @Scheduled(cron = "0 0 8 * * *")  // At 8:00 AM every day
    @Transactional
    public void generateServiceReminders() {
        log.info("Starting scheduled reminder generation job...");

        LocalDate thresholdDate = LocalDate.now().plusDays(thresholdDays);
        
        // Find all vehicles with upcoming service due date
        List<Vehicle> vehiclesWithUpcomingService = vehicleRepository
                .findAllVehiclesWithUpcomingService(thresholdDate);

        log.info("Found {} vehicles with upcoming service", vehiclesWithUpcomingService.size());

        for (Vehicle vehicle : vehiclesWithUpcomingService) {
            createDateBasedReminder(vehicle);
        }

        log.info("Reminder generation job completed.");
    }

    /**
     * Create date-based reminder for a vehicle
     */
    private void createDateBasedReminder(Vehicle vehicle) {
        // Check if reminder already exists
        if (reminderRepository.existsByVehicleIdAndReminderTypeAndIsReadFalse(
                vehicle.getId(), ReminderType.DATE_BASED)) {
            log.debug("Reminder already exists for vehicle ID: {}", vehicle.getId());
            return;
        }

        String message = String.format(
                "Service due for %s (%s) on %s",
                vehicle.getDisplayName(),
                vehicle.getRegNumber(),
                vehicle.getNextServiceDate()
        );

        Reminder reminder = Reminder.builder()
                .vehicle(vehicle)
                .user(vehicle.getUser())
                .reminderType(ReminderType.DATE_BASED)
                .dueDate(vehicle.getNextServiceDate())
                .message(message)
                .isRead(false)
                .build();

        reminderRepository.save(reminder);
        log.info("Created date-based reminder for vehicle: {}", vehicle.getRegNumber());
    }

    /**
     * Create km-based reminder for a vehicle
     * Called when odometer is updated
     */
    @Transactional
    public void checkAndCreateKmBasedReminder(Vehicle vehicle) {
        if (vehicle.getNextServiceOdometer() == null) {
            return;
        }

        int kmUntilService = vehicle.getNextServiceOdometer() - vehicle.getCurrentOdometer();

        if (kmUntilService <= thresholdKm && kmUntilService > 0) {
            // Check if reminder already exists
            if (reminderRepository.existsByVehicleIdAndReminderTypeAndIsReadFalse(
                    vehicle.getId(), ReminderType.KM_BASED)) {
                return;
            }

            String message = String.format(
                    "Service due for %s (%s) in %d km",
                    vehicle.getDisplayName(),
                    vehicle.getRegNumber(),
                    kmUntilService
            );

            Reminder reminder = Reminder.builder()
                    .vehicle(vehicle)
                    .user(vehicle.getUser())
                    .reminderType(ReminderType.KM_BASED)
                    .dueOdometer(vehicle.getNextServiceOdometer())
                    .message(message)
                    .isRead(false)
                    .build();

            reminderRepository.save(reminder);
            log.info("Created km-based reminder for vehicle: {}", vehicle.getRegNumber());
        }
    }

    /**
     * Get all active reminders for a user
     */
    public List<Reminder> getActiveReminders(Long userId) {
        return reminderRepository.findActiveReminders(userId);
    }

    /**
     * Get unread reminders count for a user
     */
    public long getUnreadReminderCount(Long userId) {
        return reminderRepository.countByUserIdAndIsReadFalse(userId);
    }

    /**
     * Mark reminder as read
     */
    @Transactional
    public void markAsRead(Long reminderId) {
        reminderRepository.findById(reminderId).ifPresent(reminder -> {
            reminder.setIsRead(true);
            reminderRepository.save(reminder);
        });
    }

    /**
     * Dismiss reminder
     */
    @Transactional
    public void dismissReminder(Long reminderId) {
        reminderRepository.findById(reminderId).ifPresent(reminder -> {
            reminder.setIsRead(true);
            reminder.setDismissedAt(java.time.LocalDateTime.now());
            reminderRepository.save(reminder);
        });
    }

    /**
     * Manual trigger for testing (also scheduled every 6 hours)
     */
    @Scheduled(fixedRate = 21600000)  // Every 6 hours (6 * 60 * 60 * 1000 ms)
    @Transactional
    public void periodicReminderCheck() {
        log.debug("Periodic reminder check running...");
        generateServiceReminders();
    }
}
