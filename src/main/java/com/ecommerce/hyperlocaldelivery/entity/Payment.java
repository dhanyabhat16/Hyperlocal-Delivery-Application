package com.ecommerce.hyperlocaldelivery.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Payment {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer paymentId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    
    @Column(nullable = false)
    private Double amount;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime paymentDate;
    
    @PrePersist
    protected void onCreate() {
        paymentDate = LocalDateTime.now();
    }
    
    public void processPayment() {
        this.status = PaymentStatus.SUCCESS;
    }
    
    public void failPayment() {
        this.status = PaymentStatus.FAILED;
    }
}
