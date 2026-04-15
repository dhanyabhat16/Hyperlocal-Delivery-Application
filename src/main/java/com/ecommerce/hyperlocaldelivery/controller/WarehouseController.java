package com.ecommerce.hyperlocaldelivery.controller;

import com.ecommerce.hyperlocaldelivery.dto.ApiResponseDTO;
import com.ecommerce.hyperlocaldelivery.entity.Warehouse;
import com.ecommerce.hyperlocaldelivery.dto.OrderDTO;
import com.ecommerce.hyperlocaldelivery.dto.ProductDTO;
import com.ecommerce.hyperlocaldelivery.dto.WarehouseAnalyticsDTO;
import com.ecommerce.hyperlocaldelivery.service.IOrderService;
import com.ecommerce.hyperlocaldelivery.service.IWarehouseService;
import com.ecommerce.hyperlocaldelivery.service.ProductService;
import com.ecommerce.hyperlocaldelivery.service.UserContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('WAREHOUSE')")
public class WarehouseController {
    private final IWarehouseService warehouseService;
    private final IOrderService orderService;
    private final ProductService productService;
    private final UserContextService userContextService;

    /**
     * Get low stock products for warehouse
     */
    @GetMapping("/products/low-stock")
    public ResponseEntity<ApiResponseDTO<List<ProductDTO>>> getLowStockProducts() {
        Warehouse warehouse = userContextService.getCurrentWarehouseOrThrow();
        List<ProductDTO> products = warehouseService.getLowStockProducts(warehouse.getWarehouseId());
        return ResponseEntity.ok(ApiResponseDTO.<List<ProductDTO>>builder()
                .statusCode(200)
                .message("Low stock products retrieved successfully")
                .data(products)
                .success(true)
                .build());
    }

    /**
     * Get orders for warehouse
     */
    @GetMapping("/orders")
    public ResponseEntity<ApiResponseDTO<List<OrderDTO>>> getWarehouseOrders() {
        Warehouse warehouse = userContextService.getCurrentWarehouseOrThrow();
        List<OrderDTO> orders = orderService.getWarehouseOrders(warehouse.getWarehouseId());
        return ResponseEntity.ok(ApiResponseDTO.<List<OrderDTO>>builder()
                .statusCode(200)
                .message("Warehouse orders retrieved successfully")
                .data(orders)
                .success(true)
                .build());
    }

    /**
     * Get order by ID for warehouse
     */
    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ApiResponseDTO<OrderDTO>> getOrderById(@PathVariable Integer orderId) {
        Warehouse warehouse = userContextService.getCurrentWarehouseOrThrow();
        OrderDTO order = orderService.getOrderByIdAndWarehouse(orderId, warehouse.getWarehouseId());
        return ResponseEntity.ok(ApiResponseDTO.<OrderDTO>builder()
                .statusCode(200)
                .message("Order retrieved successfully")
                .data(order)
                .success(true)
                .build());
    }

    /**
     * Update order status
     */
    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<ApiResponseDTO<OrderDTO>> updateOrderStatus(
            @PathVariable Integer orderId,
            @RequestParam String status) {
        Warehouse warehouse = userContextService.getCurrentWarehouseOrThrow();
        OrderDTO order = orderService.updateOrderStatus(orderId, status, warehouse.getWarehouseId());
        return ResponseEntity.ok(ApiResponseDTO.<OrderDTO>builder()
                .statusCode(200)
                .message("Order status updated successfully")
                .data(order)
                .success(true)
                .build());
    }

    @PutMapping("/products/{productId}/stock")
    public ResponseEntity<ApiResponseDTO<ProductDTO>> updateProductStock(
            @PathVariable Integer productId,
            @RequestParam Integer quantity) {
        Warehouse warehouse = userContextService.getCurrentWarehouseOrThrow();
        ProductDTO productDTO = productService.updateProductStock(productId, quantity, warehouse.getWarehouseId());
        return ResponseEntity.ok(ApiResponseDTO.<ProductDTO>builder()
                .statusCode(200)
                .message("Product stock updated successfully")
                .data(productDTO)
                .success(true)
                .build());
    }

    @PutMapping("/products/{productId}/availability")
    public ResponseEntity<ApiResponseDTO<ProductDTO>> updateProductAvailability(
            @PathVariable Integer productId,
            @RequestParam boolean available) {
        Warehouse warehouse = userContextService.getCurrentWarehouseOrThrow();
        ProductDTO productDTO = productService.updateProductAvailability(productId, available, warehouse.getWarehouseId());
        return ResponseEntity.ok(ApiResponseDTO.<ProductDTO>builder()
                .statusCode(200)
                .message("Product availability updated successfully")
                .data(productDTO)
                .success(true)
                .build());
    }

    @PutMapping("/products/{productId}")
    public ResponseEntity<ApiResponseDTO<ProductDTO>> updateWarehouseProduct(
            @PathVariable Integer productId,
            @RequestBody ProductDTO productDTO) {
        Warehouse warehouse = userContextService.getCurrentWarehouseOrThrow();
        ProductDTO updated = productService.updateProductForWarehouse(productId, productDTO, warehouse.getWarehouseId());
        return ResponseEntity.ok(ApiResponseDTO.<ProductDTO>builder()
                .statusCode(200)
                .message("Warehouse product updated successfully")
                .data(updated)
                .success(true)
                .build());
    }

    @DeleteMapping("/products/{productId}")
    public ResponseEntity<ApiResponseDTO<ProductDTO>> deleteWarehouseProduct(@PathVariable Integer productId) {
        Warehouse warehouse = userContextService.getCurrentWarehouseOrThrow();
        ProductDTO deleted = productService.deleteProductForWarehouse(productId, warehouse.getWarehouseId());
        return ResponseEntity.ok(ApiResponseDTO.<ProductDTO>builder()
                .statusCode(200)
                .message("Warehouse product deleted successfully")
                .data(deleted)
                .success(true)
                .build());
    }

    @GetMapping("/analytics")
    public ResponseEntity<ApiResponseDTO<WarehouseAnalyticsDTO>> getWarehouseAnalytics(
            @RequestParam(defaultValue = "7") Integer days) {
        Warehouse warehouse = userContextService.getCurrentWarehouseOrThrow();
        WarehouseAnalyticsDTO analyticsDTO = warehouseService.getWarehouseAnalytics(warehouse.getWarehouseId(), days);
        return ResponseEntity.ok(ApiResponseDTO.<WarehouseAnalyticsDTO>builder()
                .statusCode(200)
                .message("Warehouse analytics retrieved successfully")
                .data(analyticsDTO)
                .success(true)
                .build());
    }
}