package com.ms.paymentservice.domain.model.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PaymentDTO {

    private Long orderId;
    private LocalDateTime paymentDate;
    private String status;
    private String paymentMethod;
    private List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO {
        private String sku;
        private Integer quantity;
    }


}
