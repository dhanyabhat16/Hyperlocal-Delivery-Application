package com.ecommerce.hyperlocaldelivery.service;

import com.ecommerce.hyperlocaldelivery.dto.PaymentDTO;
import com.ecommerce.hyperlocaldelivery.dto.PaymentRequestDTO;
import com.ecommerce.hyperlocaldelivery.entity.Order;
import com.ecommerce.hyperlocaldelivery.entity.OrderStatus;
import com.ecommerce.hyperlocaldelivery.entity.Payment;
import com.ecommerce.hyperlocaldelivery.entity.PaymentStatus;
import com.ecommerce.hyperlocaldelivery.exception.InvalidOperationException;
import com.ecommerce.hyperlocaldelivery.exception.ResourceNotFoundException;
import com.ecommerce.hyperlocaldelivery.repository.OrderRepository;
import com.ecommerce.hyperlocaldelivery.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentServiceImpl implements IPaymentService {
    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    
    /**
     * Process payment for an order (mock payment)
     */
    @Override
    public PaymentDTO processPayment(PaymentRequestDTO paymentRequestDTO, Integer userId) {
        Order order = orderRepository.findById(paymentRequestDTO.getOrderId())
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + paymentRequestDTO.getOrderId()));

        if (!order.getUser().getUserId().equals(userId)) {
            throw new InvalidOperationException("Order does not belong to current user");
        }
        
        // Check if payment already exists for this order
        Payment existingPayment = paymentRepository.findByOrder(order).orElse(null);
        if (existingPayment != null && existingPayment.getStatus() == PaymentStatus.SUCCESS) {
            throw new InvalidOperationException("Payment already processed for this order");
        }
        
        // Verify the amount matches order total
        if (!paymentRequestDTO.getAmount().equals(order.getTotalAmount())) {
            throw new InvalidOperationException("Payment amount does not match order total. " +
                    "Expected: " + order.getTotalAmount() + ", Provided: " + paymentRequestDTO.getAmount());
        }
        
        // Create new payment
        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setAmount(paymentRequestDTO.getAmount());
        
        // Mock payment processing - randomly succeed or fail (90% success rate for demo)
        boolean paymentSuccess = Math.random() < 0.9;
        
        if (paymentSuccess) {
            payment.setStatus(PaymentStatus.SUCCESS);
            // Update order status to CONFIRMED
            order.setStatus(OrderStatus.CONFIRMED);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
            // Update order status to CANCELLED
            order.setStatus(OrderStatus.CANCELLED);
        }
        
        Payment savedPayment = paymentRepository.save(payment);
        orderRepository.save(order);
        
        return convertToDTO(savedPayment);
    }
    
    /**
     * Get payment by ID
     */
    @Override
    public PaymentDTO getPaymentById(Integer paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));
        return convertToDTO(payment);
    }
    
    /**
     * Get payment by order ID
     */
    @Override
    public PaymentDTO getPaymentByOrderId(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        
        Payment payment = paymentRepository.findByOrder(order)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order: " + orderId));
        
        return convertToDTO(payment);
    }

    /**
     * Get payment by ID and user
     */
    @Override
    public PaymentDTO getPaymentByIdAndUser(Integer paymentId, Integer userId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found with ID: " + paymentId));

        if (!payment.getOrder().getUser().getUserId().equals(userId)) {
            throw new InvalidOperationException("Access denied to this payment");
        }
        return convertToDTO(payment);
    }

    /**
     * Get payment by order ID and user
     */
    @Override
    public PaymentDTO getPaymentByOrderIdAndUser(Integer orderId, Integer userId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        if (!order.getUser().getUserId().equals(userId)) {
            throw new InvalidOperationException("Access denied to this payment");
        }

        Payment payment = paymentRepository.findByOrder(order)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order: " + orderId));

        return convertToDTO(payment);
    }
    
    /**
     * Helper method to convert Payment entity to DTO
     */
    private PaymentDTO convertToDTO(Payment payment) {
        return PaymentDTO.builder()
                .paymentId(payment.getPaymentId())
                .orderId(payment.getOrder().getOrderId())
                .amount(payment.getAmount())
                .status(payment.getStatus().name())
                .paymentDate(payment.getPaymentDate())
                .build();
    }
}
