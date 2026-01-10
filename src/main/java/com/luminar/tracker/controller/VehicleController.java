package com.luminar.tracker.controller;

import com.luminar.tracker.dto.VehicleDTO;
import com.luminar.tracker.entity.FuelType;
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

import java.math.BigDecimal;
import java.util.List;

/**
 * VehicleController
 * Controller for vehicle management pages.
 * 
 * Demonstrates:
 * - CRUD operations with Thymeleaf forms
 * - Path variables for RESTful URLs
 * - Model attribute for form binding
 */
@Controller
@RequestMapping("/vehicles")
@RequiredArgsConstructor
@Slf4j
public class VehicleController {

    private final VehicleService vehicleService;
    private final ServiceRecordService serviceRecordService;
    private final UserRepository userRepository;

    /**
     * Helper method to get current user
     */
    private User getCurrentUser(Authentication authentication) {
        String email = authentication.getName();
        return userRepository.findByEmail(email).orElse(null);
    }

    /**
     * List all vehicles for current user
     */
    @GetMapping
    public String listVehicles(Authentication authentication, Model model) {
        User user = getCurrentUser(authentication);
        if (user == null) return "redirect:/login";

        List<Vehicle> vehicles = vehicleService.getVehiclesByUserId(user.getId());
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("userName", user.getName());

        return "vehicles/list";
    }

    /**
     * Show add vehicle form
     */
    @GetMapping("/add")
    public String showAddVehicleForm(Model model) {
        model.addAttribute("vehicle", new VehicleDTO());
        model.addAttribute("fuelTypes", FuelType.values());
        model.addAttribute("currentYear", java.time.Year.now().getValue());
        return "vehicles/form";
    }

    /**
     * Process add vehicle form
     */
    @PostMapping("/add")
    public String addVehicle(
            @Valid @ModelAttribute("vehicle") VehicleDTO vehicleDTO,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("fuelTypes", FuelType.values());
            model.addAttribute("currentYear", java.time.Year.now().getValue());
            return "vehicles/form";
        }

        User user = getCurrentUser(authentication);
        if (user == null) return "redirect:/login";

        try {
            vehicleService.createVehicle(user.getId(), vehicleDTO);
            log.info("Vehicle added: {}", vehicleDTO.getRegNumber());
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle added successfully!");
            return "redirect:/vehicles";
        } catch (Exception e) {
            log.error("Failed to add vehicle: {}", e.getMessage());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("fuelTypes", FuelType.values());
            model.addAttribute("currentYear", java.time.Year.now().getValue());
            return "vehicles/form";
        }
    }

    /**
     * Show vehicle details
     */
    @GetMapping("/{id}")
    public String viewVehicle(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {

        User user = getCurrentUser(authentication);
        if (user == null) return "redirect:/login";

        Vehicle vehicle = vehicleService.getVehicleByIdAndUserId(id, user.getId());
        
        // Calculate stats using Java Streams (from service)
        BigDecimal totalCost = vehicleService.calculateTotalCost(id);
        long serviceCount = serviceRecordService.getServiceCountForVehicle(id);
        var serviceRecords = serviceRecordService.getServiceRecordsByVehicleId(id);

        model.addAttribute("vehicle", vehicle);
        model.addAttribute("totalCost", totalCost);
        model.addAttribute("serviceCount", serviceCount);
        model.addAttribute("serviceRecords", serviceRecords);
        model.addAttribute("userName", user.getName());

        return "vehicles/detail";
    }

    /**
     * Show edit vehicle form
     */
    @GetMapping("/{id}/edit")
    public String showEditVehicleForm(
            @PathVariable Long id,
            Authentication authentication,
            Model model) {

        User user = getCurrentUser(authentication);
        if (user == null) return "redirect:/login";

        Vehicle vehicle = vehicleService.getVehicleByIdAndUserId(id, user.getId());

        VehicleDTO vehicleDTO = VehicleDTO.builder()
                .id(vehicle.getId())
                .regNumber(vehicle.getRegNumber())
                .make(vehicle.getMake())
                .model(vehicle.getModel())
                .variant(vehicle.getVariant())
                .year(vehicle.getYear())
                .engineCapacity(vehicle.getEngineCapacity())
                .fuelType(vehicle.getFuelType())
                .purchaseDate(vehicle.getPurchaseDate())
                .currentOdometer(vehicle.getCurrentOdometer())
                .nextServiceDate(vehicle.getNextServiceDate())
                .nextServiceOdometer(vehicle.getNextServiceOdometer())
                .notes(vehicle.getNotes())
                .build();

        model.addAttribute("vehicle", vehicleDTO);
        model.addAttribute("fuelTypes", FuelType.values());
        model.addAttribute("editMode", true);

        return "vehicles/form";
    }

    /**
     * Process edit vehicle form
     */
    @PostMapping("/{id}/edit")
    public String updateVehicle(
            @PathVariable Long id,
            @Valid @ModelAttribute("vehicle") VehicleDTO vehicleDTO,
            BindingResult bindingResult,
            Authentication authentication,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("fuelTypes", FuelType.values());
            model.addAttribute("editMode", true);
            return "vehicles/form";
        }

        User user = getCurrentUser(authentication);
        if (user == null) return "redirect:/login";

        try {
            vehicleService.updateVehicle(id, user.getId(), vehicleDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle updated successfully!");
            return "redirect:/vehicles/" + id;
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("fuelTypes", FuelType.values());
            model.addAttribute("editMode", true);
            return "vehicles/form";
        }
    }

    /**
     * Delete vehicle
     */
    @PostMapping("/{id}/delete")
    public String deleteVehicle(
            @PathVariable Long id,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        User user = getCurrentUser(authentication);
        if (user == null) return "redirect:/login";

        try {
            vehicleService.deleteVehicle(id, user.getId());
            redirectAttributes.addFlashAttribute("successMessage", "Vehicle deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to delete vehicle: " + e.getMessage());
        }

        return "redirect:/vehicles";
    }
}
