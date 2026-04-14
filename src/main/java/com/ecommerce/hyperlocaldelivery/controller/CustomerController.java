package com.ecommerce.hyperlocaldelivery.controller;

import com.ecommerce.hyperlocaldelivery.dto.ApiResponseDTO;
import com.ecommerce.hyperlocaldelivery.dto.CustomerDTO;
import com.ecommerce.hyperlocaldelivery.dto.CustomerRegistrationDTO;
import com.ecommerce.hyperlocaldelivery.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerController {
    
    private final CustomerService customerService;
    
    /**
     * Register a new customer
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<CustomerDTO>> registerCustomer(@RequestBody CustomerRegistrationDTO registrationDTO) {
        CustomerDTO customerDTO = customerService.registerCustomer(registrationDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.<CustomerDTO>builder()
                        .statusCode(201)
                        .message("Customer registered successfully")
                        .data(customerDTO)
                        .success(true)
                        .build());
    }
    
    /**
     * Login customer
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<CustomerDTO>> loginCustomer(
            @RequestParam String email,
            @RequestParam String password) {
        CustomerDTO customerDTO = customerService.loginCustomer(email, password);
        return ResponseEntity.ok(ApiResponseDTO.<CustomerDTO>builder()
                .statusCode(200)
                .message("Login successful")
                .data(customerDTO)
                .success(true)
                .build());
    }
    
    /**
     * Get customer by ID
     */
    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponseDTO<CustomerDTO>> getCustomerById(@PathVariable Integer customerId) {
        CustomerDTO customerDTO = customerService.getCustomerById(customerId);
        return ResponseEntity.ok(ApiResponseDTO.<CustomerDTO>builder()
                .statusCode(200)
                .message("Customer retrieved successfully")
                .data(customerDTO)
                .success(true)
                .build());
    }
    
    /**
     * Get customer by email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponseDTO<CustomerDTO>> getCustomerByEmail(@PathVariable String email) {
        CustomerDTO customerDTO = customerService.getCustomerByEmail(email);
        return ResponseEntity.ok(ApiResponseDTO.<CustomerDTO>builder()
                .statusCode(200)
                .message("Customer retrieved successfully")
                .data(customerDTO)
                .success(true)
                .build());
    }
    
    /**
     * Update customer profile
     */
    @PutMapping("/{customerId}")
    public ResponseEntity<ApiResponseDTO<CustomerDTO>> updateCustomer(
            @PathVariable Integer customerId,
            @RequestBody CustomerDTO updateDTO) {
        CustomerDTO customerDTO = customerService.updateCustomer(customerId, updateDTO);
        return ResponseEntity.ok(ApiResponseDTO.<CustomerDTO>builder()
                .statusCode(200)
                .message("Customer updated successfully")
                .data(customerDTO)
                .success(true)
                .build());
    }
    
    /**
     * Get all customers
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<CustomerDTO>>> getAllCustomers() {
        List<CustomerDTO> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(ApiResponseDTO.<List<CustomerDTO>>builder()
                .statusCode(200)
                .message("Customers retrieved successfully")
                .data(customers)
                .success(true)
                .build());
    }
    
    /**
     * Delete customer
     */
    @DeleteMapping("/{customerId}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteCustomer(@PathVariable Integer customerId) {
        customerService.deleteCustomer(customerId);
        return ResponseEntity.ok(ApiResponseDTO.<Void>builder()
                .statusCode(200)
                .message("Customer deleted successfully")
                .success(true)
                .build());
    }
}
