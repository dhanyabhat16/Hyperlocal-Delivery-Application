package com.ecommerce.hyperlocaldelivery.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor // Good practice for JSON mapping
public class DeliveryDTO {
    private Integer deliveryId;
    private Integer orderId;
    private String status;
    private String customerName;
    private String address;      // Added field
    private List<String> itemNames; // Added field
    private Double totalAmount;  // Added field
}