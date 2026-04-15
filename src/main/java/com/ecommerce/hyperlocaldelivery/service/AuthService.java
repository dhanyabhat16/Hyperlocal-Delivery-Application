package com.ecommerce.hyperlocaldelivery.service;

import com.ecommerce.hyperlocaldelivery.dto.AuthResponseDTO;
import com.ecommerce.hyperlocaldelivery.dto.LoginDTO;
import com.ecommerce.hyperlocaldelivery.dto.RegisterDTO;

public interface AuthService {
    AuthResponseDTO register(RegisterDTO registerDTO);
    AuthResponseDTO login(LoginDTO loginDTO);
    String logout();
}
