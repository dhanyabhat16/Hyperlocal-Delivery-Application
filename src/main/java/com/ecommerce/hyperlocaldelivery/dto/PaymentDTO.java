package com.ecommerce.hyperlocaldelivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentDTO {
    private Integer paymentId;
    private Integer orderId;
    private Double amount;
    private String status;
    private LocalDateTime paymentDate;
}
