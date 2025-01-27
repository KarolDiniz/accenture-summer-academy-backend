package com.ms.orderservice.service;

import com.ms.orderservice.model.Order;
import com.ms.orderservice.model.OrderStatus;
import com.ms.orderservice.model.dto.OrderDTO;
import com.ms.orderservice.model.exception.OrderNotFoundException;
import com.ms.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusService orderStatusService;
    private final RabbitMQService rabbitMQService;
    private final OrderMapper orderMapper;

    @Transactional
    public OrderDTO createOrder(Order order) {

        order.setStatus(OrderStatus.PENDING);

        Order savedOrder = orderRepository.save(order);

        // Registra a transição de status no histórico
        orderStatusService.validateAndCreateStatusHistory(savedOrder, OrderStatus.PENDING);

        rabbitMQService.sendOrderToQueue(savedOrder);

        return orderMapper.toDTO(savedOrder);
    }

    public OrderDTO getOrder(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));

        return orderMapper.toDTO(order);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long id, String newStatus) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));

        OrderStatus requestedStatus = OrderStatus.fromString(newStatus);
        
        orderStatusService.validateAndCreateStatusHistory(order, requestedStatus);

        order.setStatus(requestedStatus);

        return orderMapper.toDTO(order);
    }

    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
            .map(orderMapper::toDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new OrderNotFoundException(id));
        orderRepository.delete(order);
    }
}
