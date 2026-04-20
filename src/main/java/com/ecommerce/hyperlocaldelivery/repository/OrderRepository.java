package com.ecommerce.hyperlocaldelivery.repository;

import com.ecommerce.hyperlocaldelivery.entity.Order;
import com.ecommerce.hyperlocaldelivery.entity.User;
import com.ecommerce.hyperlocaldelivery.entity.Warehouse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query; // IMPORTANT
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {

    List<Order> findByUser(User user);
    List<Order> findByUser_UserId(Integer userId);
    List<Order> findByUserOrderByCreatedAtDesc(User user);
    List<Order> findByWarehouse(Warehouse warehouse);
    List<Order> findByWarehouseOrderByCreatedAtDesc(Warehouse warehouse);
    List<Order> findByWarehouseAndCreatedAtAfter(Warehouse warehouse, java.time.LocalDateTime createdAt);

    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING'")
    List<Order> findUnassignedOrders();
    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING' AND o.user.city = :city")
    List<Order> findAvailableOrdersByCity(@Param("city") String city);
}