package com.ecommerce.hyperlocaldelivery.repository;

import com.ecommerce.hyperlocaldelivery.model.Category;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category,Long> {

    Category findByCategoryName(String categoryName);
}
