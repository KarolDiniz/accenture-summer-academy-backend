package com.ms.stockservice.model.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class StockOperation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private Long orderId;
    private Integer quantity;
    private String operationType; // RESERVE, CONFIRM, CANCEL
    private LocalDateTime operationDate;
    private String status;
}