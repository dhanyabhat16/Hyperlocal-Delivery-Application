package com.ecommerce.hyperlocaldelivery.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Table(name = "address")
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer addressId;

    @NotBlank
    private String street;
    @NotBlank
    private String city;
    private String doorNo;
    @NotBlank
    private String buildingName;

    @Column(nullable = false)
    private boolean isDefaultAddress = false;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    public Address(Integer addressId, String street, String city, String doorNo, String buildingName) {
        this.addressId = addressId;
        this.street = street;
        this.city = city;
        this.doorNo = doorNo;
        this.buildingName = buildingName;
    }
}
