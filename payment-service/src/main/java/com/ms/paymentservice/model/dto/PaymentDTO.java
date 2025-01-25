package com.ms.paymentservice.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PaymentDTO {

    private Long orderId;
    private String customerEmail;
    private BigDecimal amount;
    private LocalDateTime paymentDate;
    private String status;
    private String paymentMethod;
}
