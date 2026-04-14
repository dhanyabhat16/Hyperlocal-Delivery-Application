package com.ecommerce.hyperlocaldelivery.service;

import com.ecommerce.hyperlocaldelivery.dto.CustomerDTO;
import com.ecommerce.hyperlocaldelivery.dto.CustomerRegistrationDTO;
import com.ecommerce.hyperlocaldelivery.entity.Customer;
import com.ecommerce.hyperlocaldelivery.entity.Role;
import com.ecommerce.hyperlocaldelivery.exception.DuplicateEmailException;
import com.ecommerce.hyperlocaldelivery.exception.ResourceNotFoundException;
import com.ecommerce.hyperlocaldelivery.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    
    /**
     * Register a new customer
     */
    public CustomerDTO registerCustomer(CustomerRegistrationDTO registrationDTO) {
        // Check if email already exists
        if (customerRepository.findByEmail(registrationDTO.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Email already registered: " + registrationDTO.getEmail());
        }
        
        Customer customer = new Customer();
        customer.setName(registrationDTO.getName());
        customer.setEmail(registrationDTO.getEmail());
        customer.setPassword(registrationDTO.getPassword()); // In production, use BCrypt
        customer.setAddress(registrationDTO.getAddress());
        customer.setRole(Role.CUSTOMER);
        
        Customer savedCustomer = customerRepository.save(customer);
        return convertToDTO(savedCustomer);
    }
    
    /**
     * Login customer - returns CustomerDTO if credentials match
     */
    public CustomerDTO loginCustomer(String email, String password) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + email));
        
        // In production, use BCrypt password comparison
        if (!customer.getPassword().equals(password)) {
            throw new ResourceNotFoundException("Invalid password");
        }
        
        return convertToDTO(customer);
    }
    
    /**
     * Get customer by ID
     */
    public CustomerDTO getCustomerById(Integer customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));
        return convertToDTO(customer);
    }
    
    /**
     * Get customer by email
     */
    public CustomerDTO getCustomerByEmail(String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with email: " + email));
        return convertToDTO(customer);
    }
    
    /**
     * Update customer profile
     */
    public CustomerDTO updateCustomer(Integer customerId, CustomerDTO updateDTO) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));
        
        if (updateDTO.getName() != null) {
            customer.setName(updateDTO.getName());
        }
        if (updateDTO.getAddress() != null) {
            customer.setAddress(updateDTO.getAddress());
        }
        
        Customer updatedCustomer = customerRepository.save(customer);
        return convertToDTO(updatedCustomer);
    }
    
    /**
     * Get all customers
     */
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Delete customer
     */
    public void deleteCustomer(Integer customerId) {
        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException("Customer not found with ID: " + customerId);
        }
        customerRepository.deleteById(customerId);
    }
    
    /**
     * Helper method to convert Customer entity to DTO
     */
    private CustomerDTO convertToDTO(Customer customer) {
        return CustomerDTO.builder()
                .userId(customer.getUserId())
                .name(customer.getName())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .build();
    }
    
    /**
     * Get Customer entity by ID (internal use)
     */
    public Customer getCustomerEntityById(Integer customerId) {
        return customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with ID: " + customerId));
    }
}
