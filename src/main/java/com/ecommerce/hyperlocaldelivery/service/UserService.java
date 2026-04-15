package com.ecommerce.hyperlocaldelivery.service;

import com.ecommerce.hyperlocaldelivery.dto.UpdateUserDTO;
import com.ecommerce.hyperlocaldelivery.dto.UserDTO;
import com.ecommerce.hyperlocaldelivery.entity.Address;

import java.util.List;

public interface UserService {
    
    /**
     * Get user by ID
     */
    UserDTO getUserById(Integer userId);
    
    /**
     * Get all users (admin only)
     */
    List<UserDTO> getAllUsers();
    
    /**
     * Update user profile
     */
    UserDTO updateUserProfile(Integer userId, UpdateUserDTO updateUserDTO);
    
    /**
     * Delete user
     */
    String deleteUser(Integer userId);

    Address addAddress(Integer userId, Address address);

    Address updateAddress(Integer userId, Integer addressId, Address address);

    Address setDefaultAddress(Integer userId, Integer addressId);

    List<Address> getUserAddresses(Integer userId);
    
    /**
     * Helper method to convert User entity to DTO
     */
    UserDTO convertToDTO(com.ecommerce.hyperlocaldelivery.entity.User user);
}
