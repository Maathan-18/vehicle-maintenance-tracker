package com.luminar.tracker.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Reminder Entity
 * Represents a service reminder for a vehicle.
 * Generated automatically by Spring Scheduler.
 * 
 * Demonstrates:
 * - Many-to-One relationships
 * - @Enumerated for ReminderType
 * - Indexing for query optimization
 */
@Entity
@Table(name = "reminders", indexes = {
    @Index(name = "idx_reminder_user", columnList = "user_id"),
    @Index(name = "idx_reminder_vehicle", columnList = "vehicle_id"),
    @Index(name = "idx_reminder_is_read", columnList = "is_read")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Many-to-One: Many reminders belong to one vehicle
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;

    // Many-to-One: Many reminders belong to one user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "reminder_type", nullable = false, length = 20)
    private ReminderType reminderType;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "due_odometer")
    private Integer dueOdometer;

    @Column(name = "message", length = 500)
    private String message;

    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false;

    @Column(name = "dismissed_at")
    private LocalDateTime dismissedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Check if reminder is overdue
    public boolean isOverdue() {
        if (reminderType == ReminderType.DATE_BASED && dueDate != null) {
            return LocalDate.now().isAfter(dueDate);
        }
        return false;
    }

    // Get days until due (negative if overdue)
    public long getDaysUntilDue() {
        if (dueDate != null) {
            return java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
        }
        return 0;
    }
}
