package com.ms.paymentservice.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OrderDTO {

    private Long id;
    private String customerEmail;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private String status;
    
}