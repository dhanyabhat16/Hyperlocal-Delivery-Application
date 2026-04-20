package com.ecommerce.hyperlocaldelivery.service;

import com.ecommerce.hyperlocaldelivery.dto.OrderDTO;

import java.util.List;

public interface IOrderService {
    
    /**
     * Place order from cart
     */
    OrderDTO placeOrder(Integer userId);
    /**
 * Get unassigned (available) orders for delivery partners
 */
    List<OrderDTO> getAvailableOrders();
    
    /**
     * Get user's orders
     */
    List<OrderDTO> getUserOrders(Integer userId);
    
    /**
     * Get order by ID
     */
    OrderDTO getOrderById(Integer orderId);

    /**
     * Get order by ID and user
     */
    OrderDTO getOrderByIdAndUser(Integer orderId, Integer userId);

    /**
     * Get order by ID and warehouse
     */
    OrderDTO getOrderByIdAndWarehouse(Integer orderId, Integer warehouseId);
    
    /**
     * Update order status
     */
    OrderDTO updateOrderStatus(Integer orderId, String status);
    
    /**
     * Update order status (warehouse)
     */
    OrderDTO updateOrderStatus(Integer orderId, String status, Integer warehouseId);
    
    /**
     * Get all orders (admin)
     */
    List<OrderDTO> getAllOrders();
    
    /**
     * Get orders for warehouse
     */
    List<OrderDTO> getWarehouseOrders(Integer warehouseId);

    /**
     * Cancel order for the given user.
     * Allowed only when status is PENDING or PLACED.
     */
    OrderDTO cancelOrder(Integer orderId, Integer userId);

    /**
     * Get only status for an order that belongs to user.
     */
    String getOrderStatus(Integer orderId, Integer userId);

    /**Get available orders filtered by city */
    List<OrderDTO> getAvailableOrdersForCity(String city);
}
