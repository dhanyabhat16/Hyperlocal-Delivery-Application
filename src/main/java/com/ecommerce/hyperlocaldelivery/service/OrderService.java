package com.ecommerce.hyperlocaldelivery.service;

import com.ecommerce.hyperlocaldelivery.dto.OrderDTO;
import com.ecommerce.hyperlocaldelivery.dto.OrderItemDTO;
import com.ecommerce.hyperlocaldelivery.dto.ProductDTO;
import com.ecommerce.hyperlocaldelivery.entity.*;
import com.ecommerce.hyperlocaldelivery.exception.InvalidOperationException;
import com.ecommerce.hyperlocaldelivery.exception.ResourceNotFoundException;
import com.ecommerce.hyperlocaldelivery.repository.CartRepository;
import com.ecommerce.hyperlocaldelivery.repository.OrderRepository;
import com.ecommerce.hyperlocaldelivery.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final PaymentRepository paymentRepository;
    private final CustomerService customerService;
    
    /**
     * Place order - creates order from cart items
     */
    public OrderDTO placeOrder(Integer customerId) {
        Customer customer = customerService.getCustomerEntityById(customerId);
        
        // Get customer's cart
        Cart cart = cartRepository.findByCustomer(customer)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for customer: " + customerId));
        
        // Validate cart is not empty
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new InvalidOperationException("Cannot place order with empty cart");
        }
        
        // Create order
        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PLACED);
        
        // Convert cart items to order items
        List<OrderItem> orderItems = new ArrayList<>();
        Double totalAmount = 0.0;
        
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            orderItem.setOrder(order);
            orderItems.add(orderItem);
            
            totalAmount += cartItem.getPrice() * cartItem.getQuantity();
        }
        
        order.setItems(orderItems);
        order.setTotalAmount(totalAmount);
        order.placeOrder();
        
        Order savedOrder = orderRepository.save(order);
        
        // Clear cart after order is placed
        cart.getItems().clear();
        cartRepository.save(cart);
        
        return convertToDTO(savedOrder);
    }
    
    /**
     * Get order by ID
     */
    public OrderDTO getOrderById(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        return convertToDTO(order);
    }
    
    /**
     * Get all orders for a customer
     */
    public List<OrderDTO> getCustomerOrders(Integer customerId) {
        Customer customer = customerService.getCustomerEntityById(customerId);
        
        return orderRepository.findByCustomerOrderByCreatedAtDesc(customer)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Track order status
     */
    public OrderDTO trackOrder(Integer orderId) {
        return getOrderById(orderId);
    }
    
    /**
     * Cancel order - only if in PLACED status
     */
    public OrderDTO cancelOrder(Integer customerId, Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        
        // Verify order belongs to customer
        if (!order.getCustomer().getUserId().equals(customerId)) {
            throw new InvalidOperationException("Order does not belong to this customer");
        }
        
        // Validate order can be cancelled (only if PLACED)
        if (!order.getStatus().equals(OrderStatus.PLACED)) {
            throw new InvalidOperationException("Order cannot be cancelled. Current status: " + order.getStatus());
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        Order updatedOrder = orderRepository.save(order);
        
        return convertToDTO(updatedOrder);
    }
    
    /**
     * Update order status (internal use for other modules)
     */
    public void updateOrderStatus(Integer orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        
        order.setStatus(newStatus);
        orderRepository.save(order);
    }
    
    /**
     * Helper method to convert Order entity to DTO
     */
    private OrderDTO convertToDTO(Order order) {
        return OrderDTO.builder()
                .orderId(order.getOrderId())
                .status(order.getStatus().toString())
                .totalAmount(order.getTotalAmount())
                .items(order.getItems() != null ? 
                        order.getItems().stream().map(this::convertItemToDTO).collect(Collectors.toList()) :
                        null)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
    
    /**
     * Helper method to convert OrderItem to DTO
     */
    private OrderItemDTO convertItemToDTO(OrderItem item) {
        return OrderItemDTO.builder()
                .orderItemId(item.getOrderItemId())
                .product(convertProductToDTO(item.getProduct()))
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .build();
    }
    
    /**
     * Helper method to convert Product to DTO
     */
    private ProductDTO convertProductToDTO(Product product) {
        return ProductDTO.builder()
                .productId(product.getProductId())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .price(product.getPrice())
                .quantity(product.getQuantity())
                .build();
    }
}
