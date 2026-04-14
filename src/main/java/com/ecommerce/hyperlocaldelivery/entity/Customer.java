package com.ecommerce.hyperlocaldelivery.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Table(name = "customers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"carts", "orders"})
public class Customer extends User {
    
    @Column(nullable = false)
    private String address;
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cart> carts;
    
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;
    
    public Customer(Integer userId, String name, String email, String password, Role role, String address) {
        super(userId, name, email, password, role);
        this.address = address;
    }
}
