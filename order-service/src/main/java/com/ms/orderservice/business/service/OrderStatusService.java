package com.ms.orderservice.business.service;

import com.ms.orderservice.domain.dto.OrderStatusHistoryDTO;
import com.ms.orderservice.domain.entity.Order;
import com.ms.orderservice.domain.entity.OrderStatus;

import java.util.List;

public interface OrderStatusService {
    
    public abstract void validateAndCreateStatusHistory(Order order, OrderStatus requestedStatus);

    public abstract List<OrderStatusHistoryDTO> getOrderStatusHistory(Order order);
}
