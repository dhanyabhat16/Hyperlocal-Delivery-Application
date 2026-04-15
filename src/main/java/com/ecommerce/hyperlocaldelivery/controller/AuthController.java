package com.ecommerce.hyperlocaldelivery.controller;

import com.ecommerce.hyperlocaldelivery.dto.AuthResponseDTO;
import com.ecommerce.hyperlocaldelivery.dto.LoginDTO;
import com.ecommerce.hyperlocaldelivery.dto.RegisterDTO;
import com.ecommerce.hyperlocaldelivery.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody RegisterDTO dto) {
        return new ResponseEntity<>(authService.register(dto), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok(authService.logout());
    }
}
