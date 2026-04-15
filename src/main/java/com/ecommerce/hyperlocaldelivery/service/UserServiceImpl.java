package com.ecommerce.hyperlocaldelivery.service;

import com.ecommerce.hyperlocaldelivery.dto.UpdateUserDTO;
import com.ecommerce.hyperlocaldelivery.dto.UserDTO;
import com.ecommerce.hyperlocaldelivery.entity.Address;
import com.ecommerce.hyperlocaldelivery.entity.User;
import com.ecommerce.hyperlocaldelivery.exception.InvalidOperationException;
import com.ecommerce.hyperlocaldelivery.exception.ResourceNotFoundException;
import com.ecommerce.hyperlocaldelivery.repository.AddressRepository;
import com.ecommerce.hyperlocaldelivery.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final ModelMapper modelMapper;

    /**
     * Get user by ID
     */
    @Override
    public UserDTO getUserById(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        return convertToDTO(user);
    }

    /**
     * Get all users (admin only)
     */
    @Override
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Update user profile
     */
    @Override
    public UserDTO updateUserProfile(Integer userId, UpdateUserDTO updateUserDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Check if email is already taken by another user
        if(!user.getEmail().equals(updateUserDTO.getEmail())) {
            if(userRepository.findByEmail(updateUserDTO.getEmail()).isPresent()) {
                throw new RuntimeException("Email is already taken");
            }
        }

        user.setName(updateUserDTO.getName());
        user.setEmail(updateUserDTO.getEmail());

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser);
    }

    /**
     * Delete user
     */
    @Override
    public String deleteUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        userRepository.delete(user);
        return "User deleted successfully";
    }

    @Override
    public Address addAddress(Integer userId, Address address) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        address.setUser(user);

        Address savedAddress = addressRepository.save(address);
        if (user.getDefaultAddress() == null) {
            user.setDefaultAddress(savedAddress);
            savedAddress.setDefaultAddress(true);
            userRepository.save(user);
            savedAddress = addressRepository.save(savedAddress);
        }
        return savedAddress;
    }

    @Override
    public Address updateAddress(Integer userId, Integer addressId, Address address) {
        Address existing = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with ID: " + addressId));
        if (!existing.getUser().getUserId().equals(userId)) {
            throw new InvalidOperationException("Address does not belong to current user");
        }

        existing.setStreet(address.getStreet());
        existing.setCity(address.getCity());
        existing.setDoorNo(address.getDoorNo());
        existing.setBuildingName(address.getBuildingName());
        return addressRepository.save(existing);
    }

    @Override
    public Address setDefaultAddress(Integer userId, Integer addressId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found with ID: " + addressId));
        if (!address.getUser().getUserId().equals(userId)) {
            throw new InvalidOperationException("Address does not belong to current user");
        }

        List<Address> addresses = addressRepository.findByUser(user);
        for (Address existingAddress : addresses) {
            if (existingAddress.isDefaultAddress()) {
                existingAddress.setDefaultAddress(false);
                addressRepository.save(existingAddress);
            }
        }

        address.setDefaultAddress(true);
        user.setDefaultAddress(address);
        userRepository.save(user);
        return addressRepository.save(address);
    }

    @Override
    public List<Address> getUserAddresses(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        return addressRepository.findByUser(user);
    }

    /**
     * Helper method to convert User entity to DTO
     */
    @Override
    public UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
