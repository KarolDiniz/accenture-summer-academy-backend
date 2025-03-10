package com.ms.orderservice.business.service;

import com.ms.orderservice.domain.dto.OrderDTO;
import com.ms.orderservice.domain.entity.Order;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderService {
    
    public abstract OrderDTO createOrder(Order order);

    public abstract OrderDTO getOrder(Long id);

    public abstract OrderDTO updateOrderStatus(Long id, String newStatus);

    public abstract List<OrderDTO> getAllOrders();

    @Transactional
    OrderDTO updateOrder(Long id, Order updatedOrder);

    public abstract void deleteOrder(Long id);
}