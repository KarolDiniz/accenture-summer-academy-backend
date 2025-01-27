package com.ms.shippingservice.service;

import com.ms.shippingservice.model.ShipmentTracking;
import com.ms.shippingservice.model.ShippingOrder;
import com.ms.shippingservice.repository.ShipmentTrackingRepository;
import com.ms.shippingservice.repository.ShippingOrderRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShippingService {
    private final ShippingOrderRepository shippingOrderRepository;
    private final ShipmentTrackingRepository shipmentTrackingRepository;
    private final RabbitTemplate rabbitTemplate;

    public ShippingOrder createShippingOrder(ShippingOrder shippingOrder) {
        
        shippingOrder.setCreatedAt(LocalDateTime.now());
        // shippingOrder.setStatus("PENDING");
        shippingOrder.setTrackingCode(generateTrackingCode());
        shippingOrder.setEstimatedDeliveryDate(calculateEstimatedDeliveryDate());

        ShippingOrder savedOrder = shippingOrderRepository.save(shippingOrder);

        // Criar primeiro registro de tracking
        createTrackingEntry(savedOrder.getId(), "CREATED", "Order created", "Shipping center");

        // Notificar criação da ordem de envio
        rabbitTemplate.convertAndSend(
                "shipping.exchange",
                "shipping.routingkey",
                savedOrder
        );

        return savedOrder;
    }

    public ShippingOrder getShippingOrder(Long orderId) {
        return shippingOrderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Shipping order not found"));
    }

    public List<ShipmentTracking> getShippingTracking(Long shippingOrderId) {
        return shipmentTrackingRepository.findByShippingOrderIdOrderByTimestampDesc(shippingOrderId);
    }

    private void createTrackingEntry(Long shippingOrderId, String status, String description, String location) {
        ShipmentTracking tracking = new ShipmentTracking();
        tracking.setShippingOrderId(shippingOrderId);
        tracking.setStatus(status);
        tracking.setDescription(description);
        tracking.setLocation(location);
        tracking.setTimestamp(LocalDateTime.now());

        shipmentTrackingRepository.save(tracking);
    }

    private String generateTrackingCode() {
        return "TRK" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }

    private LocalDateTime calculateEstimatedDeliveryDate() {
        // Simples simulação - entrega em 5 dias
        return LocalDateTime.now().plusDays(5);
    }

    public void updateShippingStatus(Long orderId, String status, String location, String description) {
        ShippingOrder shippingOrder = getShippingOrder(orderId);
        shippingOrder.setStatus(status);
        shippingOrderRepository.save(shippingOrder);

        createTrackingEntry(shippingOrder.getId(), status, description, location);

        // Notificar atualização de status
        rabbitTemplate.convertAndSend(
                "shipping.exchange",
                "shipping.routingkey",
                shippingOrder
        );
    }
}