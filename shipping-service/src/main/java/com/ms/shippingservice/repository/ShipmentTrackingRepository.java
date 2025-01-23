package com.ms.shippingservice.repository;

import com.ms.shippingservice.model.ShipmentTracking;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ShipmentTrackingRepository extends JpaRepository<ShipmentTracking, Long> {
    List<ShipmentTracking> findByShippingOrderIdOrderByTimestampDesc(Long shippingOrderId);
}
