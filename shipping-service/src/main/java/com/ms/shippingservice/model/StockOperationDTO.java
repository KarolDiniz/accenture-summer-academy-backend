package com.ms.shippingservice.model;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class StockOperationDTO {
    private Long id;
    private Long orderId;
    private String productSku;
    private String productName;
    private Integer quantity;
    private String operationType;
    private LocalDateTime operationDate;
    private String status;
}
