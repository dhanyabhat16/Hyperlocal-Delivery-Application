package com.ecommerce.hyperlocaldelivery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DeliveryDTO {

    private Integer deliveryId;
    private Integer orderId;
    private String status;
    private String customerName;
}