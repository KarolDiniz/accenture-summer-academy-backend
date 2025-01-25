package com.ms.orderservice.service;

import com.ms.orderservice.config.RabbitMQConfig;
import com.ms.orderservice.model.Order;
import com.ms.orderservice.model.OrderStatus;
import com.ms.orderservice.model.OrderStatusHistory;
import com.ms.orderservice.model.dto.OrderDTO;
import com.ms.orderservice.model.dto.OrderStatusHistoryDTO;
import com.ms.orderservice.model.exception.InvalidStatusTransitionException;
import com.ms.orderservice.repository.OrderRepository;
import com.ms.orderservice.repository.OrderStatusHistoryRepository;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusHistoryRepository orderStatusHistoryRepository;
    private final RabbitTemplate rabbitTemplate;
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    // Método para criar o pedido e retornar o OrderDTO
    @Transactional
    public OrderDTO createOrder(Order order) {
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
            log.error("Failed to send message: ", ex);
            throw new RuntimeException("Failed to process order", ex);
        }

        // Convertendo Order para OrderDTO e retornando
        return new ModelMapper().map(savedOrder, OrderDTO.class);
    }

    // Método para buscar um pedido pelo ID e retornar como OrderDTO
    public OrderDTO getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));

        return new ModelMapper().map(order, OrderDTO.class);
    }

    // Método para atualizar o status do pedido e retornar o OrderDTO
    @Transactional
    public OrderDTO updateOrderStatus(Long id, String newStatus) {
        
        Order order = new ModelMapper().map(getOrder(id), Order.class);
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
        orderRepository.save(order);

        // Retorna o DTO do pedido com o novo status
        return new ModelMapper().map(order, OrderDTO.class);
    }

    // Método para buscar o histórico de status do pedido
    public List<OrderStatusHistoryDTO> getOrderStatusHistory(Long orderId) {
        Order order = new ModelMapper().map(getOrder(orderId), Order.class);
        List<OrderStatusHistory> historyList = orderStatusHistoryRepository.findByOrderOrderByChangeDateDesc(order);

        // Convertendo para OrderStatusHistoryDTO e retornando
        return historyList.stream()
                .map(history -> new ModelMapper().map(history, OrderStatusHistoryDTO.class))
                .collect(Collectors.toList());
    }
}
