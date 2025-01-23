package com.ms.shippingservice.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StockOperationDTO {
    private Long id;
    private Long orderId;
    private Long productId;
    private Integer quantity;
    private String operationType;
    private LocalDateTime operationDate;
    private String status;
}
