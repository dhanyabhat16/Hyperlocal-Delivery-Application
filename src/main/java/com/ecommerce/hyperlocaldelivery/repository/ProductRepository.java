package com.ecommerce.hyperlocaldelivery.repository;

import com.ecommerce.hyperlocaldelivery.entity.Product;
import com.ecommerce.hyperlocaldelivery.entity.Warehouse;
import com.ecommerce.hyperlocaldelivery.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByCategory(Category category);
    List<Product> findByNameContainingIgnoreCase(String name);
    List<Product> findByWarehouse(Warehouse warehouse);
    List<Product> findByWarehouseAndQuantityLessThan(Warehouse warehouse, Integer quantity);
}
