package com.ms.stockservice.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class StockOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private Long orderId;
    private Integer quantity;
    private String operationType; // RESERVE, CONFIRM, CANCEL
    private LocalDateTime operationDate;
    private String status;
}