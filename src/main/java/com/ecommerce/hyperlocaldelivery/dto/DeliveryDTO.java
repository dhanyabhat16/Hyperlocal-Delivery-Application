package com.ecommerce.hyperlocaldelivery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.List;

@Data
@AllArgsConstructor
public class DeliveryDTO {
    private Integer deliveryId;
    private Integer orderId;
    private String status;
    private String customerName;
    private String address;           // Added for UI
    private List<String> itemNames;   // Added for UI
    private Double totalAmount;       // Added for UI
}