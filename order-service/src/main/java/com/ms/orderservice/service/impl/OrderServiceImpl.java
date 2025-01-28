package com.ms.orderservice.service.impl;

import com.ms.orderservice.config.RabbitMQConfig;
import com.ms.orderservice.model.StockCheckDTO;
import com.ms.orderservice.model.dto.OrderDTO;
import com.ms.orderservice.model.entity.Order;
import com.ms.orderservice.model.entity.OrderStatus;
import com.ms.orderservice.model.exception.InsufficientStockException;
import com.ms.orderservice.model.exception.InvalidStatusTransitionException;
import com.ms.orderservice.model.exception.OrderNotFoundException;
import com.ms.orderservice.repository.OrderRepository;
import com.ms.orderservice.service.OrderMapper;
import com.ms.orderservice.service.OrderService;
import com.ms.orderservice.service.OrderStatusService;
import com.ms.orderservice.service.RabbitMQService;
import com.ms.orderservice.service.StockServiceClient;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderStatusService orderStatusService;
    private final RabbitMQService rabbitMQService;
    private final StockServiceClient stockServiceClient;
    private final OrderMapper orderMapper;
    private static final Logger log = LoggerFactory.getLogger(OrderServiceImpl.class);

    @Override
    @Transactional
    public OrderDTO createOrder(Order order) {
        // Verificar disponibilidade de estoque
        List<StockCheckDTO> stockChecks = order.getItems().stream()
                .map(item -> new StockCheckDTO(item.getSku(), item.getQuantity(), false))
                .toList();

        log.info("Checking stock availability for order items: {}", stockChecks);

        List<StockCheckDTO> stockResults = stockServiceClient.checkAvailability(stockChecks);

        // Verificar se todos os itens estão disponíveis
        List<String> unavailableSkus = stockResults.stream()
                .filter(result -> !result.isAvailable())
                .map(StockCheckDTO::getSku)
                .toList();

        if (!unavailableSkus.isEmpty()) {
            log.warn("Insufficient stock for items: {}", unavailableSkus);
            throw new InsufficientStockException(unavailableSkus);
        }

        // Definir status inicial do pedido
        order.setStatus(OrderStatus.PENDING);
        Order savedOrder = orderRepository.save(order);

        // Registrar o status inicial
        orderStatusService.validateAndCreateStatusHistory(savedOrder, OrderStatus.PENDING);

        // Enviar pedido para a fila
        rabbitMQService.sendOrderToQueue(savedOrder);

        return orderMapper.toDTO(savedOrder);
    }

    @Override
    public OrderDTO getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        return orderMapper.toDTO(order);
    }

    @Override
    @Transactional
    public OrderDTO updateOrderStatus(Long id, String newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        OrderStatus requestedStatus = OrderStatus.fromString(newStatus);

        // Validar transição de status
        if (!order.getStatus().canTransitionTo(requestedStatus)) {
            throw new InvalidStatusTransitionException(order.getStatus(), requestedStatus);
        }

        orderStatusService.validateAndCreateStatusHistory(order, requestedStatus);
        order.setStatus(requestedStatus);
        orderRepository.save(order);

        // Se confirmado, enviar notificação
        if (requestedStatus == OrderStatus.CONFIRMED) {
            rabbitMQService.sendOrderConfirmedNotification(order);
        }

        return orderMapper.toDTO(order);
    }

    @Override
    public List<OrderDTO> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        return orders.stream()
                .map(orderMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        orderRepository.delete(order);
    }
}
