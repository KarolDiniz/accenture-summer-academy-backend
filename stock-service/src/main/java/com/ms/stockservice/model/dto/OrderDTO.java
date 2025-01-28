package com.ms.stockservice.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private String customerEmail;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private String status;
    private List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO {
        private Long id;
        private String sku;
        private Integer quantity;
        private BigDecimal price;
    }
}