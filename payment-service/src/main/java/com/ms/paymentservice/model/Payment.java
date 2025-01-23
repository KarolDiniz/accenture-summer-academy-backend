package com.ms.paymentservice.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private String customerEmail;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private String status;
    private String paymentMethod;
}