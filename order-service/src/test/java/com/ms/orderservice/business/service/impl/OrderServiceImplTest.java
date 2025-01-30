package com.ms.orderservice.business.service.impl;

import com.ms.orderservice.domain.StockCheckDTO;
import com.ms.orderservice.domain.dto.OrderDTO;
import com.ms.orderservice.domain.entity.Order;
import com.ms.orderservice.domain.entity.OrderItem;
import com.ms.orderservice.domain.entity.OrderStatus;
import com.ms.orderservice.domain.exception.InsufficientStockException;
import com.ms.orderservice.domain.exception.InvalidStatusTransitionException;
import com.ms.orderservice.domain.exception.OrderNotFoundException;
import com.ms.orderservice.domain.repository.OrderRepository;
import com.ms.orderservice.business.service.OrderMapper;
import com.ms.orderservice.business.service.OrderStatusService;
import com.ms.orderservice.business.service.StockServiceClient;
import com.ms.orderservice.business.service.producer.RabbitMQService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;
    
    @Mock
    private OrderStatusService orderStatusService;
    
    @Mock
    private RabbitMQService rabbitMQService;
    
    @Mock
    private StockServiceClient stockServiceClient;
    
    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;
    private OrderDTO testOrderDTO;
    private OrderItem testOrderItem;

    @BeforeEach
    void setUp() {
        testOrderItem = new OrderItem();
        testOrderItem.setId(1L);
        testOrderItem.setProductId(1L);
        testOrderItem.setSku("TEST-SKU");
        testOrderItem.setQuantity(1);
        testOrderItem.setPrice(new BigDecimal("100.00"));

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setCustomerEmail("test@test.com");
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setTotalAmount(new BigDecimal("100.00"));
        testOrder.setCreatedAt(LocalDateTime.now());
        testOrder.setItems(Collections.singletonList(testOrderItem));

        testOrderDTO = new OrderDTO();
        testOrderDTO.setId(1L);
        testOrderDTO.setCustomerEmail("test@test.com");
        testOrderDTO.setStatus(OrderStatus.PENDING);
        testOrderDTO.setTotalAmount(new BigDecimal("100.00"));
    }

    @Test
    void createOrder_Success() {
        // Arrange
        StockCheckDTO stockCheck = new StockCheckDTO("TEST-SKU", 1, true);
        when(stockServiceClient.checkAvailability(any())).thenReturn(Collections.singletonList(stockCheck));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderMapper.toDTO(any(Order.class))).thenReturn(testOrderDTO);

        // Act
        OrderDTO result = orderService.createOrder(testOrder);

        // Assert
        assertNotNull(result);
        assertEquals(testOrderDTO.getId(), result.getId());
        assertEquals(OrderStatus.PENDING, result.getStatus());
        verify(rabbitMQService).sendOrderToQueue(testOrder);
        verify(orderStatusService).validateAndCreateStatusHistory(testOrder, OrderStatus.PENDING);
    }

    @Test
    void createOrder_InsufficientStock() {
        // Arrange
        StockCheckDTO stockCheck = new StockCheckDTO("TEST-SKU", 1, false);
        when(stockServiceClient.checkAvailability(any())).thenReturn(Collections.singletonList(stockCheck));

        // Act & Assert
        assertThrows(InsufficientStockException.class, () -> orderService.createOrder(testOrder));
        verify(orderRepository, never()).save(any(Order.class));
        verify(rabbitMQService, never()).sendOrderToQueue(any(Order.class));
    }

    @Test
    void updateOrderStatus_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderMapper.toDTO(any(Order.class))).thenReturn(testOrderDTO);

        // Act
        OrderDTO result = orderService.updateOrderStatus(1L, "CONFIRMED");

        // Assert
        assertNotNull(result);
        verify(orderStatusService).validateAndCreateStatusHistory(any(), eq(OrderStatus.CONFIRMED));
        verify(rabbitMQService).sendOrderConfirmedNotification(testOrder);
    }

    @Test
    void updateOrderStatus_InvalidTransition() {
        // Arrange
        testOrder.setStatus(OrderStatus.DELIVERED); // Estado final que não pode transicionar
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act & Assert
        assertThrows(InvalidStatusTransitionException.class, 
            () -> orderService.updateOrderStatus(1L, "SHIPPING"));
            
        // Verify que o orderStatusService nunca foi chamado
        verify(orderStatusService, never()).validateAndCreateStatusHistory(any(), any());
        verify(orderRepository, never()).save(any());
    }

    @Test
    void getOrder_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderMapper.toDTO(testOrder)).thenReturn(testOrderDTO);

        // Act
        OrderDTO result = orderService.getOrder(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testOrderDTO.getId(), result.getId());
    }

    @Test
    void getOrder_NotFound() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.getOrder(1L));
    }

    @Test
    void getAllOrders_Success() {
        // Arrange
        List<Order> orders = Collections.singletonList(testOrder);
        List<OrderDTO> orderDTOs = Collections.singletonList(testOrderDTO);
        when(orderRepository.findAll()).thenReturn(orders);
        when(orderMapper.toDTO(any(Order.class))).thenReturn(testOrderDTO);

        // Act
        List<OrderDTO> result = orderService.getAllOrders();

        // Assert
        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void updateOrderStatus_ToConfirmed_ShouldSendNotification() {
        // Arrange
        testOrder.setStatus(OrderStatus.PENDING);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        when(orderMapper.toDTO(any(Order.class))).thenReturn(testOrderDTO);

        // Act
        OrderDTO result = orderService.updateOrderStatus(1L, "CONFIRMED");

        // Assert
        assertNotNull(result);
        assertEquals(OrderStatus.CONFIRMED, testOrder.getStatus());
        verify(orderStatusService).validateAndCreateStatusHistory(any(), eq(OrderStatus.CONFIRMED));
        verify(orderRepository).save(any());
        verify(rabbitMQService).sendOrderConfirmedNotification(testOrder); // Essa verificação cobre a parte não testada
    }


    @Test
    void deleteOrder_Success() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act
        orderService.deleteOrder(1L);

        // Assert
        verify(orderRepository).delete(testOrder);
    }

    @Test
    void deleteOrder_NotFound() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> orderService.deleteOrder(1L));
        verify(orderRepository, never()).delete(any());
    }
}