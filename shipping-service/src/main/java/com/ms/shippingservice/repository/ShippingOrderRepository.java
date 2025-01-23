package com.ms.shippingservice.repository;

import com.ms.shippingservice.model.ShippingOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ShippingOrderRepository extends JpaRepository<ShippingOrder, Long> {
    Optional<ShippingOrder> findByOrderId(Long orderId);
    Optional<ShippingOrder> findByTrackingCode(String trackingCode);
}