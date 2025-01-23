package com.ms.orderservice.model;


import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerEmail;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private String status;

    @OneToMany(cascade = CascadeType.ALL)
    private List<OrderItem> items;
}