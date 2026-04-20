package com.ecommerce.hyperlocaldelivery.service;

import com.ecommerce.hyperlocaldelivery.dto.DeliveryDTO;
import com.ecommerce.hyperlocaldelivery.entity.Delivery;
import com.ecommerce.hyperlocaldelivery.entity.OrderStatus;

import java.util.List;

public interface IDeliveryService {

    List<DeliveryDTO> getAssignedOrders(Integer partnerId);

    Delivery acceptOrder(Integer orderId, Integer partnerId);

    Delivery updateStatus(Integer deliveryId, OrderStatus status);
}