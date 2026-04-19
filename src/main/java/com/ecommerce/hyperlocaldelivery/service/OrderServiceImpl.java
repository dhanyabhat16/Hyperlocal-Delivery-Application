package com.ecommerce.hyperlocaldelivery.service;

import com.ecommerce.hyperlocaldelivery.dto.OrderDTO;
import com.ecommerce.hyperlocaldelivery.dto.OrderItemDTO;
import com.ecommerce.hyperlocaldelivery.entity.*;
import com.ecommerce.hyperlocaldelivery.exception.InvalidOperationException;
import com.ecommerce.hyperlocaldelivery.exception.ResourceNotFoundException;
import com.ecommerce.hyperlocaldelivery.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements IOrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;
    private final IWarehouseService warehouseService;
    private final ModelMapper modelMapper;
    
    /**
     * Place order from cart
     */
    @Override
    public OrderDTO placeOrder(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        // Get city from user
        String city = user.getCity();
        if (city == null) {
            throw new InvalidOperationException("User city not set");
        }
        
        // Get warehouse for the city
        Warehouse warehouse = warehouseService.getWarehouseByCity(city);
        
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found for user: " + userId));
        
        // Check if cart is empty
        if (cart.getItems() == null || cart.getItems().isEmpty()) {
            throw new InvalidOperationException("Cannot place order with empty cart");
        }
        
        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setWarehouse(warehouse);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(0.0);
        
        Order savedOrder = orderRepository.save(order);
        
        // Create order items and reduce stock
        Double totalAmount = 0.0;
        List<OrderItem> createdItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            Product product = cartItem.getProduct();
            
            // Check if product belongs to the assigned warehouse
            if (!product.getWarehouse().getWarehouseId().equals(warehouse.getWarehouseId())) {
                throw new InvalidOperationException("Product " + product.getName() + " is not available in your city");
            }
            
            // Check if sufficient stock is available
            if (product.getQuantity() < cartItem.getQuantity()) {
                throw new InvalidOperationException("Insufficient stock for product: " + product.getName());
            }

            if (Boolean.FALSE.equals(product.getAvailable())) {
                throw new InvalidOperationException("Product unavailable: " + product.getName());
            }
            
            // Create order item
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(savedOrder);
            orderItem.setProduct(product);
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getPrice());
            
            OrderItem savedOrderItem = orderItemRepository.save(orderItem);
            createdItems.add(savedOrderItem);
            
            // Reduce product quantity
            product.setQuantity(product.getQuantity() - cartItem.getQuantity());
            productRepository.save(product);
            
            totalAmount += cartItem.getPrice() * cartItem.getQuantity();
        }
        
        // Update order total amount
        order.setTotalAmount(totalAmount);
        order.setItems(createdItems);
        Order finalOrder = orderRepository.save(order);
        
        // Clear cart items after successful order placement
        cartItemRepository.deleteByCartCartId(cart.getCartId());
        
        return convertToDTO(finalOrder);
    }
    
    /**
     * Get user's orders
     */
    @Override
    public List<OrderDTO> getUserOrders(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        return orderRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get order by ID
     */
    @Override
    public OrderDTO getOrderById(Integer orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        return convertToDTO(order);
    }

    /**
     * Get order by ID and user
     */
    @Override
    public OrderDTO getOrderByIdAndUser(Integer orderId, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        if (!order.getUser().getUserId().equals(user.getUserId())) {
            throw new InvalidOperationException("Order does not belong to the current user");
        }
        return convertToDTO(order);
    }

    /**
     * Get order by ID and warehouse
     */
    @Override
    public OrderDTO getOrderByIdAndWarehouse(Integer orderId, Integer warehouseId) {
        Warehouse warehouse = warehouseService.getWarehouseById(warehouseId);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        if (!order.getWarehouse().getWarehouseId().equals(warehouse.getWarehouseId())) {
            throw new InvalidOperationException("Order does not belong to this warehouse");
        }
        return convertToDTO(order);
    }
    
    /**
     * Update order status
     */
    @Override
    public OrderDTO updateOrderStatus(Integer orderId, String status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            order.setStatus(orderStatus);
        } catch (IllegalArgumentException e) {
            throw new InvalidOperationException("Invalid order status: " + status);
        }
        
        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }
    
    /**
     * Get all orders (admin)
     */
    @Override
    public List<OrderDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Get orders for warehouse
     */
    @Override
    public List<OrderDTO> getWarehouseOrders(Integer warehouseId) {
        Warehouse warehouse = warehouseService.getWarehouseById(warehouseId);
        return orderRepository.findByWarehouseOrderByCreatedAtDesc(warehouse)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderDTO cancelOrder(Integer orderId, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));

        if (!order.getUser().getUserId().equals(user.getUserId())) {
            throw new InvalidOperationException("Order does not belong to current user");
        }

        if (order.getStatus() != OrderStatus.PENDING && order.getStatus() != OrderStatus.PLACED) {
            throw new InvalidOperationException("Order can only be cancelled when status is PENDING or PLACED");
        }

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        order.setStatus(OrderStatus.CANCELLED);
        return convertToDTO(orderRepository.save(order));
    }
    @Override
    public List<OrderDTO> getAvailableOrders() {
    return orderRepository.findUnassignedOrders()
            .stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
}
    @Override
    public String getOrderStatus(Integer orderId, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        if (!order.getUser().getUserId().equals(user.getUserId())) {
            throw new InvalidOperationException("Order does not belong to current user");
        }
        return order.getStatus().name();
    }
    
    /**
     * Update order status (warehouse)
     */
    @Override
    public OrderDTO updateOrderStatus(Integer orderId, String status, Integer warehouseId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with ID: " + orderId));
        
        // Check if order belongs to the warehouse
        if (!order.getWarehouse().getWarehouseId().equals(warehouseId)) {
            throw new InvalidOperationException("Order does not belong to this warehouse");
        }
        
        OrderStatus newStatus;
        try {
            newStatus = OrderStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidOperationException("Invalid order status: " + status);
        }

        OrderStatus currentStatus = order.getStatus();
        if (currentStatus != OrderStatus.CANCELLED && newStatus == OrderStatus.CANCELLED) {
            // Restock products when the warehouse cancels an order
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.setQuantity(product.getQuantity() + item.getQuantity());
                productRepository.save(product);
            }
        } else if (currentStatus == OrderStatus.CANCELLED && newStatus != OrderStatus.CANCELLED) {
            // Reduce stock again if the order is reactivated from cancelled
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                if (product.getQuantity() < item.getQuantity()) {
                    throw new InvalidOperationException("Insufficient stock to reactivate order for product: " + product.getName());
                }
                product.setQuantity(product.getQuantity() - item.getQuantity());
                productRepository.save(product);
            }
        }
        
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        return convertToDTO(updatedOrder);
    }
    
    /**
     * Helper method to convert Order entity to DTO
     */
    private OrderDTO convertToDTO(Order order) {
        List<OrderItem> orderItems = order.getItems() == null ? List.of() : order.getItems();
        List<OrderItemDTO> itemDTOs = orderItems.stream()
                .map(item -> OrderItemDTO.builder()
                        .orderItemId(item.getOrderItemId())
                        .product(modelMapper.map(item.getProduct(), com.ecommerce.hyperlocaldelivery.dto.ProductDTO.class))
                        .quantity(item.getQuantity())
                        .price(item.getPrice())
                        .build())
                .collect(Collectors.toList());
                
        
        return OrderDTO.builder()
                .orderId(order.getOrderId())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .items(itemDTOs)
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}
