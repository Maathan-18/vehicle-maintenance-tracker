package com.luminar.tracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Smart Vehicle Maintenance Tracker Application
 * 
 * Main entry point for the Spring Boot application.
 * Uses @EnableScheduling for automated service reminders.
 * 
 * @author Luminar Student
 * @version 1.0.0
 */
@SpringBootApplication
@EnableScheduling
public class VehicleMaintenanceTrackerApplication {

    public static void main(String[] args) {
        SpringApplication.run(VehicleMaintenanceTrackerApplication.class, args);
        System.out.println("===========================================");
        System.out.println("Smart Vehicle Maintenance Tracker Started!");
        System.out.println("Access: http://localhost:8080");
        System.out.println("===========================================");
    }
}
