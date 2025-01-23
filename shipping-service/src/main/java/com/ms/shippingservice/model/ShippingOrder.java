package com.ms.shippingservice.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class ShippingOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private String trackingCode;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime estimatedDeliveryDate;

    @Embedded
    private Address deliveryAddress;

    private String carrierName;
    private String shippingMethod;
}