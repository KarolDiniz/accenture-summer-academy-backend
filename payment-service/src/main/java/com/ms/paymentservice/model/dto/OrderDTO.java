package com.ms.paymentservice.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

import com.ms.paymentservice.model.entity.PaymentMethod;

@Data
public class OrderDTO {

    private Long id;
    private String customerEmail;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;
    private List<OrderItemDTO> items;


    @Data
    public static class OrderItemDTO {
        private String sku;
        private Integer quantity;
        private BigDecimal price;
    }
    
    
}