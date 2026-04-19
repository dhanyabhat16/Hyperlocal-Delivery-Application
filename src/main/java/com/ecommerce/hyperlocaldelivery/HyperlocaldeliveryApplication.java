package com.ecommerce.hyperlocaldelivery;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan; // Add this import

@SpringBootApplication
@ComponentScan(basePackages = {"com.ecommerce.hyperlocaldelivery"}) // Force it to scan everything
public class HyperlocaldeliveryApplication {
    public static void main(String[] args) {
        SpringApplication.run(HyperlocaldeliveryApplication.class, args);
    }
}