package com.ms.orderservice.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

import com.ms.orderservice.domain.entity.OrderStatus;

@Data
public class OrderStatusHistoryDTO {
    private Long id;
    private OrderStatus previousStatus;
    private OrderStatus currentStatus;
    private LocalDateTime changeDate;
}
