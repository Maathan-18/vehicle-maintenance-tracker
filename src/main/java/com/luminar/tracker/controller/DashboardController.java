package com.luminar.tracker.controller;

import com.luminar.tracker.dto.DashboardDTO;
import com.luminar.tracker.entity.User;
import com.luminar.tracker.repository.UserRepository;
import com.luminar.tracker.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * DashboardController
 * Controller for the main dashboard page.
 * 
 * Demonstrates:
 * - Getting authenticated user from Spring Security
 * - Passing complex DTO to Thymeleaf template
 */
@Controller
@RequiredArgsConstructor
@Slf4j
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserRepository userRepository;

    /**
     * Show dashboard page
     */
    @GetMapping("/dashboard")
    public String showDashboard(Authentication authentication, Model model) {
        // Get logged-in user
        String email = authentication.getName();
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            return "redirect:/login";
        }

        log.info("Loading dashboard for user: {}", email);

        // Get dashboard data
        DashboardDTO dashboardData = dashboardService.getDashboardData(user.getId());

        model.addAttribute("userName", user.getName());
        model.addAttribute("dashboard", dashboardData);

        return "dashboard";
    }
}
