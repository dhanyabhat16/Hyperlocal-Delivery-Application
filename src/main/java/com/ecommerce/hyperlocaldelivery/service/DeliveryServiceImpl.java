package com.ecommerce.hyperlocaldelivery.service;

import com.ecommerce.hyperlocaldelivery.entity.Delivery;
import com.ecommerce.hyperlocaldelivery.entity.OrderStatus;
import com.ecommerce.hyperlocaldelivery.repository.DeliveryRepository;
import com.ecommerce.hyperlocaldelivery.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ecommerce.hyperlocaldelivery.dto.DeliveryDTO;
import java.util.List;

@Service
public class DeliveryServiceImpl implements IDeliveryService {

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Override
    public List<DeliveryDTO> getAssignedOrders(Integer partnerId) {
        List<Delivery> deliveries = deliveryRepository.findByDeliveryPartner_UserId(partnerId);

        return deliveries.stream().map(d ->
        new DeliveryDTO(
                d.getDeliveryId(),
                d.getOrder().getOrderId(),
                d.getStatus().name(),
                d.getOrder().getUser().getName()
        )
).toList();
    }

    @Override
    public Delivery acceptOrder(Integer deliveryId) {

        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new RuntimeException("Delivery not found"));

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