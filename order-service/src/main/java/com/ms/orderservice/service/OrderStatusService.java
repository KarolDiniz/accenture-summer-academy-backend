package com.ms.orderservice.service;

import com.ms.orderservice.model.dto.OrderStatusHistoryDTO;
import com.ms.orderservice.model.entity.Order;
import com.ms.orderservice.model.entity.OrderStatus;

import java.util.List;

public interface OrderStatusService {
    
    public abstract void validateAndCreateStatusHistory(Order order, OrderStatus requestedStatus);

    public abstract List<OrderStatusHistoryDTO> getOrderStatusHistory(Order order);
}
