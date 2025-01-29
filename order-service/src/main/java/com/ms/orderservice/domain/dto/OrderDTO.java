package com.ms.orderservice.domain.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.ms.orderservice.domain.entity.OrderStatus;

@Data
public class OrderDTO {
    private Long id;
    private String customerEmail;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private OrderStatus status;
    private String paymentMethod;
    private List<OrderItemDTO> items;  
    private List<OrderStatusHistoryDTO> orderStatusHistory;  
}
