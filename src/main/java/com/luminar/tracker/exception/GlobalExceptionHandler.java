package com.luminar.tracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * GlobalExceptionHandler
 * Centralized exception handling using @ControllerAdvice.
 * 
 * Demonstrates:
 * - @ControllerAdvice for global exception handling (per syllabus)
 * - @ExceptionHandler for specific exceptions
 * - Java Streams for validation error processing
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handle VehicleNotFoundException
     */
    @ExceptionHandler(VehicleNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleVehicleNotFound(VehicleNotFoundException ex) {
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("errorMessage", ex.getMessage());
        mav.addObject("timestamp", LocalDateTime.now());
        mav.addObject("errorType", "Vehicle Not Found");
        return mav;
    }

    /**
     * Handle UserNotFoundException
     */
    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleUserNotFound(UserNotFoundException ex) {
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("errorMessage", ex.getMessage());
        mav.addObject("timestamp", LocalDateTime.now());
        mav.addObject("errorType", "User Not Found");
        return mav;
    }

    /**
     * Handle ServiceRecordNotFoundException
     */
    @ExceptionHandler(ServiceRecordNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ModelAndView handleServiceRecordNotFound(ServiceRecordNotFoundException ex) {
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("errorMessage", ex.getMessage());
        mav.addObject("timestamp", LocalDateTime.now());
        mav.addObject("errorType", "Service Record Not Found");
        return mav;
    }

    /**
     * Handle DuplicateResourceException
     */
    @ExceptionHandler(DuplicateResourceException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ModelAndView handleDuplicateResource(DuplicateResourceException ex) {
        ModelAndView mav = new ModelAndView("error/error");
        mav.addObject("errorMessage", ex.getMessage());
        mav.addObject("timestamp", LocalDateTime.now());
        mav.addObject("errorType", "Duplicate Resource");
        return mav;
    }

    /**
     * Handle Validation Errors
     * Uses Java Streams to collect field errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModelAndView handleValidationErrors(MethodArgumentNotValidException ex) {
        // Using Java Streams (JDK 8 feature) to collect validation errors
        Map<String, String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        error -> error.getField(),
                        error -> error.getDefaultMessage() != null ? error.getDefaultMessage() : "Invalid value",
                        (existing, replacement) -> existing
                ));

        ModelAndView mav = new ModelAndView("error/validation");
        mav.addObject("errors", errors);
        mav.addObject("timestamp", LocalDateTime.now());
        mav.addObject("errorType", "Validation Error");
        return mav;
    }

    /**
     * Handle Generic Exceptions
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ModelAndView handleGenericException(Exception ex) {
        ModelAndView mav = new ModelAndView("error/500");
        mav.addObject("errorMessage", "An unexpected error occurred. Please try again later.");
        mav.addObject("timestamp", LocalDateTime.now());
        mav.addObject("errorType", "Internal Server Error");
        // Log the actual exception for debugging
        ex.printStackTrace();
        return mav;
    }
}
