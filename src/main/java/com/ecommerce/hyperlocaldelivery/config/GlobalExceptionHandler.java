package com.ecommerce.hyperlocaldelivery.config;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.ecommerce.hyperlocaldelivery.dto.ApiResponseDTO;
import com.ecommerce.hyperlocaldelivery.exception.DuplicateEmailException;
import com.ecommerce.hyperlocaldelivery.exception.InsufficientStockException;
import com.ecommerce.hyperlocaldelivery.exception.InvalidOperationException;
import com.ecommerce.hyperlocaldelivery.exception.ResourceNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    /**
     * Handle ResourceNotFoundException
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDTO.builder()
                        .statusCode(404)
                        .message(ex.getMessage())
                        .success(false)
                        .build());
    }
    
    /**
     * Handle InsufficientStockException
     */
    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleInsufficientStockException(
            InsufficientStockException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.builder()
                        .statusCode(400)
                        .message(ex.getMessage())
                        .success(false)
                        .build());
    }
    
    /**
     * Handle InvalidOperationException
     */
    @ExceptionHandler(InvalidOperationException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleInvalidOperationException(
            InvalidOperationException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponseDTO.builder()
                        .statusCode(400)
                        .message(ex.getMessage())
                        .success(false)
                        .build());
    }
    
    /**
     * Handle DuplicateEmailException
     */
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleDuplicateEmailException(
            DuplicateEmailException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponseDTO.builder()
                        .statusCode(409)
                        .message(ex.getMessage())
                        .success(false)
                        .build());
    }

    /**
     * Handle missing static resources / unmapped root paths
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleNoResourceFoundException(
            NoResourceFoundException ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponseDTO.builder()
                        .statusCode(404)
                        .message("Resource not found: " + ex.getResourcePath())
                        .success(false)
                        .build());
    }
    
    /**
     * Handle general exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseDTO<Object>> handleGlobalException(
            Exception ex, WebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponseDTO.builder()
                        .statusCode(500)
                        .message("An unexpected error occurred: " + ex.getMessage())
                        .success(false)
                        .build());
    }
}
