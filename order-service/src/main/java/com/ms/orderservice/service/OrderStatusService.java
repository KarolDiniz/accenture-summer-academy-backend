package com.ms.orderservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ms.orderservice.model.Order;
import com.ms.orderservice.model.OrderStatus;
import com.ms.orderservice.model.OrderStatusHistory;
import com.ms.orderservice.model.exception.InvalidStatusTransitionException;
import com.ms.orderservice.repository.OrderRepository;
import com.ms.orderservice.repository.OrderStatusHistoryRepository;

@Service
public class OrderStatusService {

    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final OrderRepository orderRepository;

    public OrderStatusService(OrderStatusHistoryRepository orderStatusHistoryRepository, OrderRepository orderRepository) {
        this.orderStatusHistoryRepository = orderStatusHistoryRepository;
        this.orderRepository = orderRepository;
    }

    public void updateOrderStatus(Order order, OrderStatus requestedStatus) {

        OrderStatus previousStatus = order.getStatus();

        // Validação da transição de status

        if (!previousStatus.canTransitionTo(requestedStatus)) {
            throw new InvalidStatusTransitionException(previousStatus, requestedStatus);
        }

        // Registrar a mudança de status
        
        OrderStatusHistory orderStatusHistory = new OrderStatusHistory(order, previousStatus, requestedStatus);
        orderStatusHistoryRepository.save(orderStatusHistory);

        order.setStatus(requestedStatus);
        orderRepository.save(order);
    }

    public List<OrderStatusHistory> getOrderStatusHistory(Order order) {
        return orderStatusHistoryRepository.findByOrderOrderByChangeDateDesc(order);
    }
}
