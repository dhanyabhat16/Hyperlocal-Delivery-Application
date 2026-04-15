package com.ecommerce.hyperlocaldelivery.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaceOrderDTO {
    @NotNull(message = "User ID is required")
    private Integer userId;
}
