package com.ecommerce.hyperlocaldelivery.controller;

import com.ecommerce.hyperlocaldelivery.dto.ApiResponseDTO;
import com.ecommerce.hyperlocaldelivery.dto.OrderDTO;
import com.ecommerce.hyperlocaldelivery.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOrderController {
    private final IOrderService orderService;

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<OrderDTO>>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(ApiResponseDTO.<List<OrderDTO>>builder()
                .statusCode(200)
                .message("All orders retrieved successfully")
                .data(orders)
                .success(true)
                .build());
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<ApiResponseDTO<OrderDTO>> updateOrderStatus(
            @PathVariable Integer orderId,
            @RequestParam String status) {
        OrderDTO orderDTO = orderService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(ApiResponseDTO.<OrderDTO>builder()
                .statusCode(200)
                .message("Order status updated successfully")
                .data(orderDTO)
                .success(true)
                .build());
    }
}