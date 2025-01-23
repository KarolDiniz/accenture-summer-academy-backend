package com.ms.shippingservice.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class ShipmentTracking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long shippingOrderId;
    private String status;
    private String location;
    private String description;
    private LocalDateTime timestamp;
}