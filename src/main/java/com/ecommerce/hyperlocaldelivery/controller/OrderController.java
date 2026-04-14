package com.ecommerce.hyperlocaldelivery.controller;

import com.ecommerce.hyperlocaldelivery.dto.ApiResponseDTO;
import com.ecommerce.hyperlocaldelivery.dto.OrderDTO;
import com.ecommerce.hyperlocaldelivery.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class OrderController {
    
    private final OrderService orderService;
    
    /**
     * Place order from cart
     */
    @PostMapping("/{customerId}/place-order")
    public ResponseEntity<ApiResponseDTO<OrderDTO>> placeOrder(@PathVariable Integer customerId) {
        OrderDTO orderDTO = orderService.placeOrder(customerId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.<OrderDTO>builder()
                        .statusCode(201)
                        .message("Order placed successfully")
                        .data(orderDTO)
                        .success(true)
                        .build());
    }
    
    /**
     * Get order by ID
     */
    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponseDTO<OrderDTO>> getOrderById(@PathVariable Integer orderId) {
        OrderDTO orderDTO = orderService.getOrderById(orderId);
        return ResponseEntity.ok(ApiResponseDTO.<OrderDTO>builder()
                .statusCode(200)
                .message("Order retrieved successfully")
                .data(orderDTO)
                .success(true)
                .build());
    }
    
    /**
     * Get all orders for a customer
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponseDTO<List<OrderDTO>>> getCustomerOrders(@PathVariable Integer customerId) {
        List<OrderDTO> orders = orderService.getCustomerOrders(customerId);
        return ResponseEntity.ok(ApiResponseDTO.<List<OrderDTO>>builder()
                .statusCode(200)
                .message("Customer orders retrieved successfully")
                .data(orders)
                .success(true)
                .build());
    }
    
    /**
     * Track order status
     */
    @GetMapping("/{orderId}/track")
    public ResponseEntity<ApiResponseDTO<OrderDTO>> trackOrder(@PathVariable Integer orderId) {
        OrderDTO orderDTO = orderService.trackOrder(orderId);
        return ResponseEntity.ok(ApiResponseDTO.<OrderDTO>builder()
                .statusCode(200)
                .message("Order tracked successfully")
                .data(orderDTO)
                .success(true)
                .build());
    }
    
    /**
     * Cancel order
     */
    @DeleteMapping("/{customerId}/orders/{orderId}/cancel")
    public ResponseEntity<ApiResponseDTO<OrderDTO>> cancelOrder(
            @PathVariable Integer customerId,
            @PathVariable Integer orderId) {
        OrderDTO orderDTO = orderService.cancelOrder(customerId, orderId);
        return ResponseEntity.ok(ApiResponseDTO.<OrderDTO>builder()
                .statusCode(200)
                .message("Order cancelled successfully")
                .data(orderDTO)
                .success(true)
                .build());
    }
}
