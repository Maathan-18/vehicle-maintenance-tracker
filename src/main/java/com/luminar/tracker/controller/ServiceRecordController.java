package com.luminar.tracker.controller;

import com.luminar.tracker.dto.ServiceRecordDTO;
import com.luminar.tracker.entity.ServiceRecord;
import com.luminar.tracker.entity.ServiceType;
import com.luminar.tracker.entity.User;
import com.luminar.tracker.entity.Vehicle;
import com.luminar.tracker.repository.UserRepository;
import com.luminar.tracker.service.ServiceRecordService;
import com.luminar.tracker.service.VehicleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

/**
 * ServiceRecordController
 * Controller for service record (maintenance) management pages.
 * 
 * Demonstrates:
 * - Nested resource handling (services belong to vehicles)
 * - Date handling in forms
 */
@Controller
@RequestMapping("/services")
@RequiredArgsConstructor
@Slf4j
public class ServiceRecordController {

    private final ServiceRecordService serviceRecordService;
    private final VehicleService vehicleService;
    private final UserRepository userRepository;

    /**
     * Helper method to get current user
     */
    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElse(null);
    }

    /**
     * List all service records for current user
     */
    @GetMapping
    public String listServices(Authentication authentication, Model model) {
        User user = getCurrentUser(authentication);
        if (user == null)
            return "redirect:/login";

        List<ServiceRecord> services = serviceRecordService.getServiceRecordsByUserId(user.getId());
        java.math.BigDecimal totalCost = serviceRecordService.getTotalCostForUser(user.getId());

        model.addAttribute("services", services);
        model.addAttribute("totalCost", totalCost);
        model.addAttribute("userName", user.getName());

        return "services/list";
    }

    /**
     * Show add service form (with optional vehicle pre-selected)
     */
    @GetMapping("/add")
    public String showAddServiceForm(
            @RequestParam(required = false) Long vehicleId,
            Authentication authentication,
            Model model) {

        User user = getCurrentUser(authentication);
        if (user == null)
            return "redirect:/login";

        List<Vehicle> vehicles = vehicleService.getVehiclesByUserId(user.getId());

        ServiceRecordDTO serviceDTO = ServiceRecordDTO.builder()
                .vehicleId(vehicleId)
                .serviceDate(LocalDate.now())
                .build();

        model.addAttribute("service", serviceDTO);
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("serviceTypes", ServiceType.values());

        return "services/form";
    }

    /**
     * Process add service form
     */
    @PostMapping("/add")
    public String addService(
            @Valid @ModelAttribute("service") ServiceRecordDTO serviceDTO,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(authentication);
        if (user == null)
            return "redirect:/login";

        if (bindingResult.hasErrors()) {
            List<Vehicle> vehicles = vehicleService.getVehiclesByUserId(user.getId());
            model.addAttribute("vehicles", vehicles);
            model.addAttribute("serviceTypes", ServiceType.values());
            return "services/form";
        }

        try {
            serviceRecordService.createServiceRecord(user.getId(), serviceDTO);
            log.info("Service record added for vehicle ID: {}", serviceDTO.getVehicleId());
            redirectAttributes.addFlashAttribute("successMessage", "Service record added successfully!");
            return "redirect:/vehicles/" + serviceDTO.getVehicleId();
        } catch (Exception e) {
            log.error("Failed to add service record: {}", e.getMessage());
            List<Vehicle> vehicles = vehicleService.getVehiclesByUserId(user.getId());
            model.addAttribute("vehicles", vehicles);
            model.addAttribute("serviceTypes", ServiceType.values());
            model.addAttribute("errorMessage", e.getMessage());
            return "services/form";
        }
    }

    /**
     * Show edit service form
     */
    @GetMapping("/{id}/edit")
    public String showEditServiceForm(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {

        User user = getCurrentUser(authentication);
        if (user == null)
            return "redirect:/login";

        ServiceRecord record = serviceRecordService.getServiceRecordById(id);
        List<Vehicle> vehicles = vehicleService.getVehiclesByUserId(user.getId());

        ServiceRecordDTO serviceDTO = ServiceRecordDTO.builder()
                .id(record.getId())
                .vehicleId(record.getVehicle().getId())
                .serviceDate(record.getServiceDate())
                .odometerReading(record.getOdometerReading())
                .serviceType(record.getServiceType())
                .description(record.getDescription())
                .cost(record.getCost())
                .serviceCenterName(record.getServiceCenterName())
                .nextServiceDate(record.getNextServiceDate())
                .nextServiceOdometer(record.getNextServiceOdometer())
                .paymentMethod(record.getPaymentMethod())
                .build();

        model.addAttribute("service", serviceDTO);
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("serviceTypes", ServiceType.values());
        model.addAttribute("editMode", true);

        return "services/form";
    }

    /**
     * Process edit service form
     */
    @PostMapping("/{id}/edit")
    public String updateService(
            @PathVariable Long id,
            @Valid @ModelAttribute("service") ServiceRecordDTO serviceDTO,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(authentication);
        if (user == null)
            return "redirect:/login";

        if (bindingResult.hasErrors()) {
            List<Vehicle> vehicles = vehicleService.getVehiclesByUserId(user.getId());
            model.addAttribute("vehicles", vehicles);
            model.addAttribute("serviceTypes", ServiceType.values());
            model.addAttribute("editMode", true);
            return "services/form";
        }

        try {
            serviceRecordService.updateServiceRecord(id, user.getId(), serviceDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Service record updated successfully!");
            return "redirect:/vehicles/" + serviceDTO.getVehicleId();
        } catch (Exception e) {
            List<Vehicle> vehicles = vehicleService.getVehiclesByUserId(user.getId());
            model.addAttribute("vehicles", vehicles);
            model.addAttribute("serviceTypes", ServiceType.values());
            model.addAttribute("editMode", true);
            model.addAttribute("errorMessage", e.getMessage());
            return "services/form";
        }
    }

    /**
     * Delete service record
     */
    @PostMapping("/{id}/delete")
    public String deleteService(
            @PathVariable Long id,
            @RequestParam Long vehicleId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(authentication);
        if (user == null)
            return "redirect:/login";

        try {
            serviceRecordService.deleteServiceRecord(id, user.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Service record deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete service record: " + e.getMessage());
        }

        return "redirect:/vehicles/" + vehicleId;
    }
}
