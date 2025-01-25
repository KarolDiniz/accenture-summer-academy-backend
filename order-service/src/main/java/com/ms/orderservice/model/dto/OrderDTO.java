package com.ms.orderservice.model.dto;

import com.ms.orderservice.model.OrderStatus;

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
    private OrderStatus status;
    private String paymentMethod;
    private List<OrderItemDTO> items;  
    private List<OrderStatusHistoryDTO> orderStatusHistory;  
}
