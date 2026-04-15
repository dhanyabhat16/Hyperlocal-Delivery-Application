package com.ecommerce.hyperlocaldelivery.controller;

import com.ecommerce.hyperlocaldelivery.dto.ApiResponseDTO;
import com.ecommerce.hyperlocaldelivery.entity.User;
import com.ecommerce.hyperlocaldelivery.dto.OrderDTO;
import com.ecommerce.hyperlocaldelivery.service.IOrderService;
import com.ecommerce.hyperlocaldelivery.service.UserContextService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/customer/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('CUSTOMER')")
public class OrderController {
    private final IOrderService orderService;
    private final UserContextService userContextService;
    
    /**
     * Place order from cart
     */
    @PostMapping("/place")
    public ResponseEntity<ApiResponseDTO<OrderDTO>> placeOrder() {
        User user = userContextService.getCurrentUserOrThrow();
        OrderDTO orderDTO = orderService.placeOrder(user.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.<OrderDTO>builder()
                        .statusCode(201)
                        .message("Order placed successfully")
                        .data(orderDTO)
                        .success(true)
                        .build());
    }
    
    /**
     * Get user's orders
     */
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<OrderDTO>>> getUserOrders() {
        User user = userContextService.getCurrentUserOrThrow();
        List<OrderDTO> orders = orderService.getUserOrders(user.getUserId());
        return ResponseEntity.ok(ApiResponseDTO.<List<OrderDTO>>builder()
                .statusCode(200)
                .message("User orders retrieved successfully")
                .data(orders)
                .success(true)
                .build());
    }
    
    /**
     * Get order by ID
     */
    @GetMapping("/details/{orderId}")
    public ResponseEntity<ApiResponseDTO<OrderDTO>> getOrderById(@PathVariable Integer orderId) {
        User user = userContextService.getCurrentUserOrThrow();
        OrderDTO orderDTO = orderService.getOrderByIdAndUser(orderId, user.getUserId());
        return ResponseEntity.ok(ApiResponseDTO.<OrderDTO>builder()
                .statusCode(200)
                .message("Order retrieved successfully")
                .data(orderDTO)
                .success(true)
                .build());
    }

    @PutMapping("/cancel/{orderId}")
    public ResponseEntity<ApiResponseDTO<OrderDTO>> cancelOrder(@PathVariable Integer orderId) {
        User user = userContextService.getCurrentUserOrThrow();
        OrderDTO orderDTO = orderService.cancelOrder(orderId, user.getUserId());
        return ResponseEntity.ok(ApiResponseDTO.<OrderDTO>builder()
                .statusCode(200)
                .message("Order cancelled successfully")
                .data(orderDTO)
                .success(true)
                .build());
    }

    @GetMapping("/{orderId}/status")
    public ResponseEntity<ApiResponseDTO<String>> getOrderStatus(@PathVariable Integer orderId) {
        User user = userContextService.getCurrentUserOrThrow();
        String status = orderService.getOrderStatus(orderId, user.getUserId());
        return ResponseEntity.ok(ApiResponseDTO.<String>builder()
                .statusCode(200)
                .message("Order status retrieved successfully")
                .data(status)
                .success(true)
                .build());
    }
    
}
