package com.ecommerce.hyperlocaldelivery.repository;

import com.ecommerce.hyperlocaldelivery.entity.Order;
import com.ecommerce.hyperlocaldelivery.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    Optional<Payment> findByOrderOrderId(Integer orderId);
    Optional<Payment> findByOrder(Order order);
}
