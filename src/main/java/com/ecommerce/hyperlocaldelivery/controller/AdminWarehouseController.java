package com.ecommerce.hyperlocaldelivery.controller;

import com.ecommerce.hyperlocaldelivery.dto.ApiResponseDTO;
import com.ecommerce.hyperlocaldelivery.dto.WarehouseDTO;
import com.ecommerce.hyperlocaldelivery.service.IWarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/warehouses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminWarehouseController {
    private final IWarehouseService warehouseService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<WarehouseDTO>>> getAllWarehouses() {
        List<WarehouseDTO> warehouses = warehouseService.getAllWarehouses();
        return ResponseEntity.ok(ApiResponseDTO.<List<WarehouseDTO>>builder()
                .statusCode(200)
                .message("Warehouses retrieved successfully")
                .data(warehouses)
                .success(true)
                .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<WarehouseDTO>> createWarehouse(@RequestBody WarehouseDTO warehouseDTO) {
        WarehouseDTO createdWarehouse = warehouseService.createWarehouse(warehouseDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.<WarehouseDTO>builder()
                        .statusCode(201)
                        .message("Warehouse created successfully")
                        .data(createdWarehouse)
                        .success(true)
                        .build());
    }
}
