package com.ms.orderservice.service;


import com.ms.orderservice.config.RabbitMQConfig;
import com.ms.orderservice.model.Order;
import com.ms.orderservice.model.OrderStatus;
import com.ms.orderservice.model.OrderStatusHistory;
import com.ms.orderservice.model.exception.InvalidStatusTransitionException;
import com.ms.orderservice.repository.OrderRepository;
import com.ms.orderservice.repository.OrderStatusHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final RabbitTemplate rabbitTemplate;
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);


    @Transactional
    public Order createOrder(Order order) {
        order.setStatus(OrderStatus.PENDING);
        Order savedOrder = orderRepository.save(order);

        // Registrar o status inicial
        OrderStatusHistory initialStatus = new OrderStatusHistory(savedOrder, null, OrderStatus.PENDING);
        orderStatusHistoryRepository.save(initialStatus);

        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDER_EXCHANGE,
                    RabbitMQConfig.ORDER_ROUTING_KEY,
                    savedOrder
            );
            log.info("Message sent to {} with routing key {}: {}",
                    RabbitMQConfig.ORDER_EXCHANGE,
                    RabbitMQConfig.ORDER_ROUTING_KEY,
                    savedOrder);
        } catch (Exception ex) {
            log.error("Failed to sent message: ", ex);
            throw new RuntimeException("Failed to process order", ex);
        }

        return savedOrder;
    }

    public Order getOrder(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    @Transactional
    public Order updateOrderStatus(Long id, String newStatus) {
        Order order = getOrder(id);
        OrderStatus requestedStatus = OrderStatus.fromString(newStatus);
        OrderStatus previousStatus = order.getStatus();

        if (!order.getStatus().canTransitionTo(requestedStatus)) {
            throw new InvalidStatusTransitionException(
                    order.getStatus().toString(),
                    requestedStatus.toString());
        }

        // Registrar a mudança de status
        OrderStatusHistory orderStatusHistory = new OrderStatusHistory(order, previousStatus, requestedStatus);
        orderStatusHistoryRepository.save(orderStatusHistory);

        order.setStatus(requestedStatus);
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = getOrder(id); // Reutilizando o método existente para buscar o pedido
        orderRepository.delete(order);
        log.info("Order with id {} has been deleted.", id);
    }


    public List<OrderStatusHistory> getOrderStatusHistory(Long orderId) {
        Order order = getOrder(orderId);
        return orderStatusHistoryRepository.findByOrderOrderByChangeDateDesc(order);
    }

}