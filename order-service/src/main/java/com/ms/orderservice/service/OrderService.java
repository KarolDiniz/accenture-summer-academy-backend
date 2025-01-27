package com.ms.orderservice.service;

import com.ms.orderservice.model.Order;
import com.ms.orderservice.model.OrderStatus;
import com.ms.orderservice.model.OrderStatusHistory;
import com.ms.orderservice.model.dto.OrderDTO;
import com.ms.orderservice.model.dto.OrderItemDTO;
import com.ms.orderservice.model.dto.OrderStatusHistoryDTO;
import com.ms.orderservice.model.exception.OrderNotFoundException;
import com.ms.orderservice.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
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
    private final ModelMapper modelMapper;

    // Método para criar o pedido e retornar o OrderDTO
    @Transactional
    public OrderDTO createOrder(Order order) {
        order.setStatus(OrderStatus.PENDING);
        Order savedOrder = orderRepository.save(order);

        // Registrar o status inicial
        orderStatusService.updateOrderStatus(savedOrder, OrderStatus.PENDING);

        // Enviar a mensagem para a fila
        rabbitMQService.sendOrderToQueue(savedOrder);

        // Convertendo Order para OrderDTO e retornando
        return convertToDTO(savedOrder);
    }

    // Método para buscar um pedido pelo ID e retornar como OrderDTO
    public OrderDTO getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        return convertToDTO(order);
    }

    // Método para atualizar o status do pedido e retornar o OrderDTO
    @Transactional
    public OrderDTO updateOrderStatus(Long id, String newStatus) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));

        OrderStatus requestedStatus = OrderStatus.fromString(newStatus);

        // Delegar para o OrderStatusService para atualizar o status
        orderStatusService.updateOrderStatus(order, requestedStatus);

        return convertToDTO(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
        orderRepository.delete(order);
    }

    // Método para buscar o histórico de status do pedido
    public List<OrderStatusHistoryDTO> getOrderStatusHistory(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        List<OrderStatusHistory> historyList = orderStatusService.getOrderStatusHistory(order);

        return historyList.stream()
                .map(history -> modelMapper.map(history, OrderStatusHistoryDTO.class))
                .collect(Collectors.toList());
    }

    // Método auxiliar para converter Order para OrderDTO
    private OrderDTO convertToDTO(Order order) {
        OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);
        
        // Mapeando os itens do pedido e o histórico de status
        List<OrderItemDTO> itemDTOs = order.getItems().stream()
                .map(item -> modelMapper.map(item, OrderItemDTO.class))
                .collect(Collectors.toList());
        
        List<OrderStatusHistoryDTO> statusHistoryDTOs = order.getOrderStatusHistory().stream()
                .map(history -> modelMapper.map(history, OrderStatusHistoryDTO.class))
                .collect(Collectors.toList());

        orderDTO.setItems(itemDTOs);
        orderDTO.setOrderStatusHistory(statusHistoryDTOs);

        return orderDTO;
    }
}
