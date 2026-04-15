package com.ecommerce.hyperlocaldelivery.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentRequestDTO {
    @NotNull(message = "Order ID is required")
    private Integer orderId;

    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount must be greater than 0")
    private Double amount;
}
