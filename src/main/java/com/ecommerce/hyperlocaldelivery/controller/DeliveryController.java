package com.ecommerce.hyperlocaldelivery.controller;

import com.ecommerce.hyperlocaldelivery.entity.OrderStatus;
import com.ecommerce.hyperlocaldelivery.service.IDeliveryService;
import com.ecommerce.hyperlocaldelivery.service.IOrderService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/delivery")
public class DeliveryController {

    @Autowired
    private IDeliveryService deliveryService;
    @Autowired
    private IOrderService orderService;

    // Get all assigned orders for delivery partner
    @GetMapping("/my-orders")
    public ResponseEntity<?> getMyOrders(@RequestParam Integer partnerId) {
        return ResponseEntity.ok(
                deliveryService.getAssignedOrders(partnerId)
        );
    }

    // Accept delivery
    @GetMapping("/accept/{deliveryId}")
    public ResponseEntity<?> acceptOrder(@PathVariable Integer deliveryId) {
        return ResponseEntity.ok(
                deliveryService.acceptOrder(deliveryId)
        );
    }

    // Update delivery status
    @GetMapping("/update-status/{deliveryId}")
    public ResponseEntity<?> updateStatus(
            @PathVariable Integer deliveryId,
            @RequestParam OrderStatus status) {

        return ResponseEntity.ok(
                deliveryService.updateStatus(deliveryId, status)
        );
    }
    @GetMapping("/available")
    public ResponseEntity<?> getAvailableOrders() {
    return ResponseEntity.ok(
            orderService.getAvailableOrders()
    );
}
}