package com.ecommerce.hyperlocaldelivery.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDTO {
    private Integer addressId;

    @NotBlank
    private String street;

    @NotBlank
    private String city;

    private String doorNo;

    @NotBlank
    private String buildingName;

    private boolean defaultAddress;
}
