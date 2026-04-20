package com.ecommerce.hyperlocaldelivery.service;

import com.ecommerce.hyperlocaldelivery.dto.AuthResponseDTO;
import com.ecommerce.hyperlocaldelivery.dto.LoginDTO;
import com.ecommerce.hyperlocaldelivery.dto.RegisterDTO;
import com.ecommerce.hyperlocaldelivery.entity.Role;
import com.ecommerce.hyperlocaldelivery.entity.User;
import com.ecommerce.hyperlocaldelivery.repository.UserRepository;
import com.ecommerce.hyperlocaldelivery.config.AppConstant;
import com.ecommerce.hyperlocaldelivery.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponseDTO register(RegisterDTO registerDTO) {

        // Validate city
        if (registerDTO.getCity() == null || !AppConstant.SUPPORTED_CITIES.contains(registerDTO.getCity())) {
            throw new IllegalArgumentException("Service not available in this city: " + registerDTO.getCity());
        }

        // check if user exists
        if(userRepository.findByEmail(registerDTO.getEmail()).isPresent()) {
            throw new RuntimeException("User already exists");
        }

        // create user
        User user = new User();
        user.setName(registerDTO.getName());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        user.setRole(Role.valueOf(registerDTO.getRole().toUpperCase()));
        user.setCity(registerDTO.getCity()); // Store city for customers

        User savedUser = userRepository.save(user);

        String token = jwtUtil.generateToken(savedUser.getEmail(), savedUser.getRole().name(), savedUser.getCity());

        return mapToDTO(savedUser, token);
    }

    
    @Override
    public AuthResponseDTO login(LoginDTO loginDTO) {
        // Trim the email to remove accidental leading/trailing spaces
        String cleanedEmail = loginDTO.getEmail().trim();
        
        // Add a temporary console log to see EXACTLY what Hibernate is searching for
        System.out.println("Login attempt for email: [" + cleanedEmail + "]");

        User user = userRepository.findByEmail(cleanedEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if(!passwordEncoder.matches(loginDTO.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name(), user.getCity());

        return mapToDTO(user, token);
    }

    @Override
    public String logout() {
        return "Logged out successfully";
    }

    private AuthResponseDTO mapToDTO(User user, String token) {
        return AuthResponseDTO.builder()
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .token(token)
                .build();
    }
}