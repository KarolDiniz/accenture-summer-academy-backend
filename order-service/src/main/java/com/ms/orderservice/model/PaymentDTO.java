package com.ms.orderservice.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PaymentDTO {
    private Long orderId;
    private String status;

    public PaymentDTO() {
    }

    public PaymentDTO(Long orderId, String status) {
        this.orderId = orderId;
        this.status = status;
    }
}
