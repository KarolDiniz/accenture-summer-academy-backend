package com.ms.orderservice.business.service.impl;

import com.ms.orderservice.domain.StockCheckDTO;
import com.ms.orderservice.domain.dto.OrderDTO;
import com.ms.orderservice.domain.entity.Order;
import com.ms.orderservice.domain.entity.OrderStatus;
import com.ms.orderservice.domain.exception.InsufficientStockException;
import com.ms.orderservice.domain.exception.InvalidStatusTransitionException;
import com.ms.orderservice.domain.exception.OrderNotFoundException;
import com.ms.orderservice.domain.repository.OrderRepository;
import com.ms.orderservice.business.service.OrderMapper;
import com.ms.orderservice.business.service.OrderService;
import com.ms.orderservice.business.service.OrderStatusService;
import com.ms.orderservice.business.service.StockServiceClient;
import com.ms.orderservice.business.service.producer.RabbitMQService;

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
    public OrderDTO updateOrder(Long id, Order updatedOrder) {
        // Primeiro, verifica se a ordem existe
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        // Verificar disponibilidade de estoque para os novos itens
        List<StockCheckDTO> stockChecks = updatedOrder.getItems().stream()
                .map(item -> new StockCheckDTO(item.getSku(), item.getQuantity(), false))
                .toList();

        log.info("Checking stock availability for updated order items: {}", stockChecks);

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

        // Atualizar campos permitidos
        existingOrder.setCustomerEmail(updatedOrder.getCustomerEmail());

        // Limpar itens existentes e adicionar novos
        existingOrder.getItems().clear();
        existingOrder.getItems().addAll(updatedOrder.getItems());

        // Manter o status atual
        Order savedOrder = orderRepository.save(existingOrder);
        return orderMapper.toDTO(savedOrder);
    }

    @Override
    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        orderRepository.delete(order);
    }
}
