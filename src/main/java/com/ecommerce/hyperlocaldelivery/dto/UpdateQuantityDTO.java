package com.ecommerce.hyperlocaldelivery.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateQuantityDTO {
    private Integer cartItemId;
    private Integer quantity;
}
