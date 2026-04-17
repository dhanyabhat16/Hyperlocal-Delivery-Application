package com.ecommerce.hyperlocaldelivery.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer deliveryId;

    @OneToOne
    private Order order;

    @ManyToOne
    private User deliveryPartner;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}