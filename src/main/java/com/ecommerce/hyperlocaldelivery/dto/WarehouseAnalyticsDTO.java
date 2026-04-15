package com.ecommerce.hyperlocaldelivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseAnalyticsDTO {
    private Integer warehouseId;
    private String warehouseName;
    private String city;
    private double totalSales;
    private long orderCount;
    private double averageSalesPerDay;
    private Integer days;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
