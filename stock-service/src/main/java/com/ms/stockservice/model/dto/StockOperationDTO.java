package com.ms.stockservice.model.dto;


import com.ms.stockservice.model.entity.StockOperation;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StockOperationDTO {
    private Long id;
    private Long orderId;
    private String productSku;
    private String productName;
    private Integer quantity;
    private String operationType;
    private LocalDateTime operationDate;
    private String status;

    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_FAILED = "FAILED";
    public static final String STATUS_PROCESSING = "PROCESSING";

    public static final String OPERATION_RESERVE = "RESERVE";
    public static final String OPERATION_CANCEL = "CANCEL";
    public static final String OPERATION_CONFIRM = "CONFIRM";

    public static StockOperationDTO fromOperation(StockOperation operation) {
        return StockOperationDTO.builder()
                .id(operation.getId())
                .orderId(operation.getOrderId())
                .productSku(operation.getProduct().getSku())
                .productName(operation.getProduct().getName())
                .quantity(operation.getQuantity())
                .operationType(operation.getOperationType())
                .operationDate(operation.getOperationDate())
                .status(operation.getStatus())
                .build();
    }
}