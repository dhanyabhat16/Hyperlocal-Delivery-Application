package com.ecommerce.hyperlocaldelivery.entity;

import com.ecommerce.hyperlocaldelivery.model.Category;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer productId;
    
    @Column(nullable = false)
    @NotBlank
    @Size(min=3,message="Product name must have atleast 3 chars")
    private String name;
    
    @Column(length = 500)
    @NotBlank
    @Size(min=4,message="Product name must have atleast 4 chars")
    private String description;

    @Column(nullable = false)
    private Double price;

    private String image;
    
    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne//multiple products mapped to one product
    @JoinColumn(name = "category_id")
    private Category category;
}
