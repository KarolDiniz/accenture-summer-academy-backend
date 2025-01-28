package com.ms.paymentservice.model.dto;

import lombok.Data;
import java.math.BigDecimal;

import com.ms.paymentservice.model.entity.PaymentMethod;

@Data
public class OrderDTO {

    private Long id;
    private String customerEmail;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    
    
}