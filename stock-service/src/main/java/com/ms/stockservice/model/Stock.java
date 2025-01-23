package com.ms.stockservice.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;
    private Integer quantity;
    private String productName;
    private String sku;

    @Version
    private Long version;
}