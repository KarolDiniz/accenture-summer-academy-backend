package com.ms.orderservice.service;

import com.ms.orderservice.model.dto.OrderDTO;
import com.ms.orderservice.model.entity.Order;

import java.util.List;
import java.util.stream.Collectors;

public interface OrderService {
    
    public abstract OrderDTO createOrder(Order order);

    public abstract OrderDTO getOrder(Long id);

    public abstract OrderDTO updateOrderStatus(Long id, String newStatus);

    public abstract List<OrderDTO> getAllOrders();

    public abstract void deleteOrder(Long id);
}