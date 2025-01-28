package com.ms.orderservice.model.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderItemDTO {

    private Long id;
    private Long productId;
    private String sku;
    private Integer quantity;
    private BigDecimal price;

}