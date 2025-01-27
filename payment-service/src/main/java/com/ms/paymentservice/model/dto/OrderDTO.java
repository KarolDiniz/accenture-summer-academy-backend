package com.ms.paymentservice.model.dto;

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
<<<<<<< HEAD:payment-service/src/main/java/com/ms/paymentservice/model/dto/OrderDTO.java
    private String paymentMethod;
    
=======
    private List<OrderItemDTO> items;

    @Data
    public static class OrderItemDTO {
        private Long id;
        private String sku;
        private Integer quantity;
        private BigDecimal price;
    }
>>>>>>> origin/feature/stock-validations:payment-service/src/main/java/com/ms/paymentservice/model/OrderDTO.java
}