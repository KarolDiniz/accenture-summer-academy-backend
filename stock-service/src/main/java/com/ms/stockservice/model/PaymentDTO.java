package com.ms.stockservice.model;

import lombok.Data;
import lombok.ToString;
import java.util.List;

@Data
@ToString
public class PaymentDTO {
    private Long id;
    private Long orderId;
    private String status;
    private List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO {
        private String sku;
        private Integer quantity;
    }

    public PaymentDTO() {
    }

    public PaymentDTO(Long orderId, String status) {
        this.orderId = orderId;
        this.status = status;
    }
}