package com.ecommerce.hyperlocaldelivery.dto;

import com.ecommerce.hyperlocaldelivery.entity.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponseDTO {
    private Integer userId;
    private String name;
    private String email;
    private String role;
    private String token;
}
