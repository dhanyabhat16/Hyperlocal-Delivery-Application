package com.ecommerce.hyperlocaldelivery.repository;

import com.ecommerce.hyperlocaldelivery.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Integer> {
    Optional<CartItem> findByCartCartIdAndProductProductId(Integer cartId, Integer productId);
    List<CartItem> findByCartCartId(Integer cartId);
    long deleteByCartCartId(Integer cartId);
}
