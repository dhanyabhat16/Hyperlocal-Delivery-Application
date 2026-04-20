package com.ecommerce.hyperlocaldelivery.service;

import com.ecommerce.hyperlocaldelivery.entity.Delivery;
import com.ecommerce.hyperlocaldelivery.entity.OrderStatus;
import com.ecommerce.hyperlocaldelivery.repository.DeliveryRepository;
import com.ecommerce.hyperlocaldelivery.repository.OrderRepository;
import com.ecommerce.hyperlocaldelivery.repository.UserRepository;
import com.ecommerce.hyperlocaldelivery.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ecommerce.hyperlocaldelivery.dto.DeliveryDTO;
import java.util.List;

@Service
public class DeliveryServiceImpl implements IDeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public List<DeliveryDTO> getAssignedOrders(Integer partnerId) {
        List<Delivery> deliveries = deliveryRepository.findByDeliveryPartner_UserId(partnerId);

        return deliveries.stream().map(d -> {
    List<String> itemNames = d.getOrder().getItems().stream()
            .map(item -> item.getProduct().getName())
            .toList();

    return new DeliveryDTO(
            d.getDeliveryId(),
            d.getOrder().getOrderId(),
            d.getStatus().name(),
            d.getOrder().getUser().getName(),
            d.getOrder().getUser().getCity(), // Matches 'address'
            itemNames,                        // Matches 'itemNames'
            d.getOrder().getTotalAmount()     // Matches 'totalAmount'
    );
}).toList();}

    @Override
    public Delivery acceptOrder(Integer orderId, Integer partnerId) {

    Delivery delivery = deliveryRepository.findAll()
            .stream()
            .filter(d -> d.getOrder().getOrderId().equals(orderId))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Delivery not found"));

    User partner = userRepository.findById(partnerId)
            .orElseThrow(() -> new RuntimeException("User not found"));

    delivery.setDeliveryPartner(partner);
    delivery.setStatus(OrderStatus.ASSIGNED);

    return deliveryRepository.save(delivery);
}

    
    

    @Override
    public Delivery updateStatus(Integer deliveryId, OrderStatus status) {

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

        delivery.setStatus(status);

        delivery.getOrder().updateStatus(status);

        return deliveryRepository.save(delivery);
    }
}