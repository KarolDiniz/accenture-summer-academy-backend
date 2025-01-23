package com.ms.stockservice.model;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentDTO {
    private Long id;
    private Long orderId;
    private String customerEmail;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private String status;
    private String paymentMethod;
}