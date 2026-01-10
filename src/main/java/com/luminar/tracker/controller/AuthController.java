package com.luminar.tracker.controller;

import com.luminar.tracker.dto.UserRegistrationDTO;
import com.luminar.tracker.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * AuthController
 * Controller for authentication-related pages (login, register).
 * 
 * Demonstrates:
 * - @Controller for MVC with Thymeleaf
 * - Form handling and validation
 * - Redirect with flash attributes
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    /**
     * Home page - redirects to login or dashboard
     */
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    /**
     * Show login page
     */
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    /**
     * Show registration page
     */
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new UserRegistrationDTO());
        return "register";
    }

    /**
     * Process registration form
     */
    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("user") UserRegistrationDTO registrationDTO,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {

        // Check for validation errors
        if (bindingResult.hasErrors()) {
            return "register";
        }

        // Check if passwords match
        if (!registrationDTO.getPassword().equals(registrationDTO.getConfirmPassword())) {
            bindingResult.rejectValue("confirmPassword", "error.user", "Passwords do not match");
            return "register";
        }

        // Check if email already exists
        if (userService.emailExists(registrationDTO.getEmail())) {
            bindingResult.rejectValue("email", "error.user", "Email already registered");
            return "register";
        }

        try {
            userService.registerUser(registrationDTO);
            log.info("User registered successfully: {}", registrationDTO.getEmail());
            redirectAttributes.addFlashAttribute("successMessage", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            log.error("Registration failed: {}", e.getMessage());
            model.addAttribute("errorMessage", "Registration failed. Please try again.");
            return "register";
        }
    }
}
