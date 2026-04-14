package com.ecommerce.hyperlocaldelivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemDTO {
    private Integer cartItemId;
    private ProductDTO product;
    private Integer quantity;
    private Double price;
}
