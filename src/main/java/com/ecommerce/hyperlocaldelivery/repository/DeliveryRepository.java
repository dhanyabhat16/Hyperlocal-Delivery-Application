package com.ecommerce.hyperlocaldelivery.repository;

import com.ecommerce.hyperlocaldelivery.entity.Delivery;
import com.ecommerce.hyperlocaldelivery.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Integer> {

    // Get deliveries assigned to a delivery partner
    List<Delivery> findByDeliveryPartner_UserId(Integer userId);

    // Optional: Get deliveries by status
    List<Delivery> findByStatus(OrderStatus status);
}