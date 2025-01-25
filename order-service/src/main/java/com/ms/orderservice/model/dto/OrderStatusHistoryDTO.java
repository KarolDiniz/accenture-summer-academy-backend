package com.ms.orderservice.model.dto;

import com.ms.orderservice.model.OrderStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderStatusHistoryDTO {
    private Long id;
    private OrderStatus previousStatus;
    private OrderStatus currentStatus;
    private LocalDateTime changeDate;
}
