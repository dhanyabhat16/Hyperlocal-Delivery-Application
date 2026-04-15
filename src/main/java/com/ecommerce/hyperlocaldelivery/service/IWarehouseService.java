package com.ecommerce.hyperlocaldelivery.service;

import com.ecommerce.hyperlocaldelivery.dto.ProductDTO;
import com.ecommerce.hyperlocaldelivery.dto.WarehouseAnalyticsDTO;
import com.ecommerce.hyperlocaldelivery.dto.WarehouseDTO;

import java.util.List;

public interface IWarehouseService {

    com.ecommerce.hyperlocaldelivery.entity.Warehouse getWarehouseByCity(String city);
    com.ecommerce.hyperlocaldelivery.entity.Warehouse getWarehouseById(Integer warehouseId);

    List<ProductDTO> getLowStockProducts(Integer warehouseId);

    WarehouseAnalyticsDTO getWarehouseAnalytics(Integer warehouseId, Integer days);
    List<com.ecommerce.hyperlocaldelivery.dto.WarehouseDTO> getAllWarehouses();
    com.ecommerce.hyperlocaldelivery.dto.WarehouseDTO createWarehouse(com.ecommerce.hyperlocaldelivery.dto.WarehouseDTO warehouseDTO);
}