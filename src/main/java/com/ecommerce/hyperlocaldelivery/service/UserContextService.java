package com.ecommerce.hyperlocaldelivery.service;

import com.ecommerce.hyperlocaldelivery.entity.User;
import com.ecommerce.hyperlocaldelivery.entity.Warehouse;
import com.ecommerce.hyperlocaldelivery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserContextService {
    private final UserRepository userRepository;
    private final IWarehouseService warehouseService;

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof org.springframework.security.core.userdetails.User) {
            org.springframework.security.core.userdetails.User userDetails = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            return userRepository.findByEmail(userDetails.getUsername()).orElse(null);
        }
        return null;
    }

    public Warehouse getCurrentWarehouse() {
        User user = getCurrentUser();
        if (user != null) {
            if (user.getRole() == com.ecommerce.hyperlocaldelivery.entity.Role.WAREHOUSE) {
                if (user.getWarehouse() != null) {
                    return user.getWarehouse();
                }
                if (user.getCity() == null || user.getCity().isBlank()) {
                    return null;
                }
                try {
                    return warehouseService.getWarehouseByCity(user.getCity());
                } catch (RuntimeException ex) {
                    return null;
                }
            } else if (user.getRole() == com.ecommerce.hyperlocaldelivery.entity.Role.CUSTOMER) {
                if (user.getCity() == null || user.getCity().isBlank()) {
                    return null;
                }
                try {
                    return warehouseService.getWarehouseByCity(user.getCity());
                } catch (RuntimeException ex) {
                    // For customer-facing public product APIs, missing warehouse mapping should not fail the request.
                    return null;
                }
            }
        }
        return null;
    }

    public User getCurrentUserOrThrow() {
        User user = getCurrentUser();
        if (user == null) {
            throw new AuthenticationCredentialsNotFoundException("Authentication required");
        }
        return user;
    }

    public Warehouse getCurrentWarehouseOrThrow() {
        Warehouse warehouse = getCurrentWarehouse();
        if (warehouse == null) {
            throw new AuthenticationCredentialsNotFoundException("Authenticated warehouse context required");
        }
        return warehouse;
    }
}