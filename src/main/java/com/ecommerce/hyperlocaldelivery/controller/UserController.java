package com.ecommerce.hyperlocaldelivery.controller;

import com.ecommerce.hyperlocaldelivery.dto.ApiResponseDTO;
import com.ecommerce.hyperlocaldelivery.dto.AddressDTO;
import com.ecommerce.hyperlocaldelivery.dto.UpdateUserDTO;
import com.ecommerce.hyperlocaldelivery.dto.UserDTO;
import com.ecommerce.hyperlocaldelivery.entity.Address;
import com.ecommerce.hyperlocaldelivery.entity.User;
import com.ecommerce.hyperlocaldelivery.service.UserContextService;
import com.ecommerce.hyperlocaldelivery.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.modelmapper.ModelMapper;

import java.util.List;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('CUSTOMER')")
public class UserController {
    private final UserService userService;
    private final UserContextService userContextService;
    private final ModelMapper modelMapper;
    
    /**
     * Get current user profile
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponseDTO<UserDTO>> getCurrentUser() {
        User currentUser = userContextService.getCurrentUserOrThrow();
        UserDTO userDTO = userService.getUserById(currentUser.getUserId());
        return ResponseEntity.ok(ApiResponseDTO.<UserDTO>builder()
                .statusCode(200)
                .message("User profile retrieved successfully")
                .data(userDTO)
                .success(true)
                .build());
    }
    
    /**
     * Update current user profile
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponseDTO<UserDTO>> updateUserProfile(
            @Valid @RequestBody UpdateUserDTO updateUserDTO) {
        User currentUser = userContextService.getCurrentUserOrThrow();
        UserDTO userDTO = userService.updateUserProfile(currentUser.getUserId(), updateUserDTO);
        return ResponseEntity.ok(ApiResponseDTO.<UserDTO>builder()
                .statusCode(200)
                .message("User profile updated successfully")
                .data(userDTO)
                .success(true)
                .build());
    }
    
    /**
     * Delete current user
     */
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponseDTO<String>> deleteUser() {
        User currentUser = userContextService.getCurrentUserOrThrow();
        String result = userService.deleteUser(currentUser.getUserId());
        return ResponseEntity.ok(ApiResponseDTO.<String>builder()
                .statusCode(200)
                .message(result)
                .success(true)
                .build());
    }

    @GetMapping("/address")
    public ResponseEntity<ApiResponseDTO<List<AddressDTO>>> getAddresses() {
        User currentUser = userContextService.getCurrentUserOrThrow();
        List<AddressDTO> addresses = userService.getUserAddresses(currentUser.getUserId())
                .stream()
                .map(address -> modelMapper.map(address, AddressDTO.class))
                .toList();
        return ResponseEntity.ok(ApiResponseDTO.<List<AddressDTO>>builder()
                .statusCode(200)
                .message("Addresses retrieved successfully")
                .data(addresses)
                .success(true)
                .build());
    }

    @PostMapping("/address")
    public ResponseEntity<ApiResponseDTO<AddressDTO>> addAddress(@Valid @RequestBody AddressDTO addressDTO) {
        User currentUser = userContextService.getCurrentUserOrThrow();
        Address address = modelMapper.map(addressDTO, Address.class);
        Address savedAddress = userService.addAddress(currentUser.getUserId(), address);
        return ResponseEntity.ok(ApiResponseDTO.<AddressDTO>builder()
                .statusCode(200)
                .message("Address added successfully")
                .data(modelMapper.map(savedAddress, AddressDTO.class))
                .success(true)
                .build());
    }

    @PutMapping("/address/{addressId}")
    public ResponseEntity<ApiResponseDTO<AddressDTO>> updateAddress(
            @PathVariable Integer addressId,
            @Valid @RequestBody AddressDTO addressDTO) {
        User currentUser = userContextService.getCurrentUserOrThrow();
        Address updatedAddress = userService.updateAddress(currentUser.getUserId(), addressId, modelMapper.map(addressDTO, Address.class));
        return ResponseEntity.ok(ApiResponseDTO.<AddressDTO>builder()
                .statusCode(200)
                .message("Address updated successfully")
                .data(modelMapper.map(updatedAddress, AddressDTO.class))
                .success(true)
                .build());
    }

    @PutMapping("/address/{addressId}/default")
    public ResponseEntity<ApiResponseDTO<AddressDTO>> setDefaultAddress(@PathVariable Integer addressId) {
        User currentUser = userContextService.getCurrentUserOrThrow();
        Address defaultAddress = userService.setDefaultAddress(currentUser.getUserId(), addressId);
        return ResponseEntity.ok(ApiResponseDTO.<AddressDTO>builder()
                .statusCode(200)
                .message("Default address updated successfully")
                .data(modelMapper.map(defaultAddress, AddressDTO.class))
                .success(true)
                .build());
    }
}
