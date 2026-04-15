package com.ecommerce.hyperlocaldelivery.service;

import com.ecommerce.hyperlocaldelivery.dto.PaymentDTO;
import com.ecommerce.hyperlocaldelivery.dto.PaymentRequestDTO;

public interface IPaymentService {
    
    /**
     * Process payment for an order (mock payment)
     */
    PaymentDTO processPayment(PaymentRequestDTO paymentRequestDTO, Integer userId);
    
    /**
     * Get payment by ID
     */
    PaymentDTO getPaymentById(Integer paymentId);
    
    /**
     * Get payment by order ID
     */
    PaymentDTO getPaymentByOrderId(Integer orderId);

    /**
     * Get payment by ID and user
     */
    PaymentDTO getPaymentByIdAndUser(Integer paymentId, Integer userId);

    /**
     * Get payment by order ID and user
     */
    PaymentDTO getPaymentByOrderIdAndUser(Integer orderId, Integer userId);
}
