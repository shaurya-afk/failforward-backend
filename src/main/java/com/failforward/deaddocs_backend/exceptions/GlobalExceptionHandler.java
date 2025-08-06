package com.failforward.deaddocs_backend.exceptions;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolation(
            DataIntegrityViolationException ex, WebRequest request) {
        
        Map<String, String> errorResponse = new HashMap<>();
        String message = ex.getMessage();
        
        // Check for specific constraint violations
        if (message != null) {
            if (message.contains("value too long for type character varying")) {
                errorResponse.put("error", "Data too long for database field");
                errorResponse.put("message", "One of the provided values exceeds the maximum allowed length");
                errorResponse.put("details", "Please check that user IDs, names, and other text fields are within the allowed limits");
            } else if (message.contains("duplicate key")) {
                errorResponse.put("error", "Duplicate entry");
                errorResponse.put("message", "This record already exists");
            } else {
                errorResponse.put("error", "Database constraint violation");
                errorResponse.put("message", "The data provided violates database constraints");
            }
        } else {
            errorResponse.put("error", "Database error");
            errorResponse.put("message", "A database constraint was violated");
        }
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Invalid input");
        errorResponse.put("message", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    @ExceptionHandler(StoryNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleStoryNotFoundException(
            StoryNotFoundException ex, WebRequest request) {
        
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Story not found");
        errorResponse.put("message", ex.getMessage());
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGenericException(
            Exception ex, WebRequest request) {
        
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", "Internal server error");
        errorResponse.put("message", "An unexpected error occurred");
        
        // Log the actual exception for debugging
        System.err.println("Unexpected error: " + ex.getMessage());
        ex.printStackTrace();
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
} 