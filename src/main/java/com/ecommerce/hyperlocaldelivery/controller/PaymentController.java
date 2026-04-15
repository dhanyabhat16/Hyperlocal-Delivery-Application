package com.ecommerce.hyperlocaldelivery.controller;

import com.ecommerce.hyperlocaldelivery.dto.ApiResponseDTO;
import com.ecommerce.hyperlocaldelivery.dto.PaymentDTO;
import com.ecommerce.hyperlocaldelivery.dto.PaymentRequestDTO;
import com.ecommerce.hyperlocaldelivery.entity.User;
import com.ecommerce.hyperlocaldelivery.service.IPaymentService;
import com.ecommerce.hyperlocaldelivery.service.UserContextService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer/payment")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('CUSTOMER')")
public class PaymentController {
    private final IPaymentService paymentService;
    private final UserContextService userContextService;
    
    /**
     * Process payment for an order
     */
    @PostMapping("/process")
    public ResponseEntity<ApiResponseDTO<PaymentDTO>> processPayment(
            @Valid @RequestBody PaymentRequestDTO paymentRequestDTO) {
        User user = userContextService.getCurrentUserOrThrow();
        PaymentDTO paymentDTO = paymentService.processPayment(paymentRequestDTO, user.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponseDTO.<PaymentDTO>builder()
                        .statusCode(201)
                        .message("Payment processed successfully")
                        .data(paymentDTO)
                        .success(true)
                        .build());
    }
    
    /**
     * Get payment by ID
     */
    @GetMapping("/{paymentId}")
    public ResponseEntity<ApiResponseDTO<PaymentDTO>> getPaymentById(@PathVariable Integer paymentId) {
        User user = userContextService.getCurrentUserOrThrow();
        PaymentDTO paymentDTO = paymentService.getPaymentByIdAndUser(paymentId, user.getUserId());
        return ResponseEntity.ok(ApiResponseDTO.<PaymentDTO>builder()
                .statusCode(200)
                .message("Payment retrieved successfully")
                .data(paymentDTO)
                .success(true)
                .build());
    }
    
    /**
     * Get payment by order ID
     */
    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponseDTO<PaymentDTO>> getPaymentByOrderId(@PathVariable Integer orderId) {
        User user = userContextService.getCurrentUserOrThrow();
        PaymentDTO paymentDTO = paymentService.getPaymentByOrderIdAndUser(orderId, user.getUserId());
        return ResponseEntity.ok(ApiResponseDTO.<PaymentDTO>builder()
                .statusCode(200)
                .message("Payment retrieved successfully")
                .data(paymentDTO)
                .success(true)
                .build());
    }
}
