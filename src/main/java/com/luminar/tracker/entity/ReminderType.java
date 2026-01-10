package com.luminar.tracker.entity;

/**
 * ReminderType Enum
 * Represents the type of service reminder.
 * 
 * DATE_BASED - Reminder based on next service due date
 * KM_BASED - Reminder based on odometer/mileage threshold
 */
public enum ReminderType {
    DATE_BASED("Date Based"),
    KM_BASED("Kilometer Based");

    private final String displayName;

    ReminderType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
