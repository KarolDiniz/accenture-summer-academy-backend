package com.ms.orderservice.presentation.controller;

import com.ms.orderservice.domain.dto.OrderDTO;
import com.ms.orderservice.domain.dto.OrderStatusHistoryDTO;
import com.ms.orderservice.domain.entity.Order;
import com.ms.orderservice.domain.entity.OrderStatus;
import com.ms.orderservice.domain.exception.OrderNotFoundException;
import com.ms.orderservice.business.service.OrderMapper;
import com.ms.orderservice.business.service.OrderService;
import com.ms.orderservice.business.service.OrderStatusService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private OrderStatusService orderStatusService;

    @Mock
    private OrderMapper orderMapper;

    @InjectMocks
    private OrderController orderController;

    private OrderDTO testOrderDTO;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrderDTO = new OrderDTO();
        testOrderDTO.setId(1L);
        testOrderDTO.setCustomerEmail("test@test.com");
        testOrderDTO.setStatus(OrderStatus.PENDING);
        testOrderDTO.setTotalAmount(new BigDecimal("100.00"));
        testOrderDTO.setCreatedAt(LocalDateTime.now());

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setCustomerEmail("test@test.com");
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setTotalAmount(new BigDecimal("100.00"));
    }

    @Test
    void createOrder_Success() {
        // Arrange
        when(orderService.createOrder(any())).thenReturn(testOrderDTO);

        // Act
        ResponseEntity<OrderDTO> response = orderController.createOrder(testOrder);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testOrderDTO.getId(), response.getBody().getId());
    }

    @Test
    void updateOrderStatus_Success() {
        // Arrange
        when(orderService.updateOrderStatus(1L, "CONFIRMED")).thenReturn(testOrderDTO);

        // Act
        ResponseEntity<OrderDTO> response = orderController.updateOrderStatus(1L, "CONFIRMED");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(orderService).updateOrderStatus(1L, "CONFIRMED");
    }

    @Test
    void getOrder_Success() {
        // Arrange
        when(orderService.getOrder(1L)).thenReturn(testOrderDTO);

        // Act
        ResponseEntity<OrderDTO> response = orderController.getOrder(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(testOrderDTO.getId(), response.getBody().getId());
    }

    @Test
    void getAllOrders_Success() {
        // Arrange
        List<OrderDTO> orderDTOs = Arrays.asList(testOrderDTO);
        when(orderService.getAllOrders()).thenReturn(orderDTOs);

        // Act
        ResponseEntity<List<OrderDTO>> response = orderController.getAllOrders();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }

    @Test
    void deleteOrder_Success() {
        // Arrange
        doNothing().when(orderService).deleteOrder(1L);

        // Act
        ResponseEntity<Void> response = orderController.deleteOrder(1L);

        // Assert
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(orderService).deleteOrder(1L);
    }

    @Test
    void getOrderStatusHistory_Success() {
        // Arrange
        List<OrderStatusHistoryDTO> historyDTOs = Arrays.asList(new OrderStatusHistoryDTO());
        when(orderService.getOrder(1L)).thenReturn(testOrderDTO);
        when(orderMapper.toEntity(testOrderDTO)).thenReturn(testOrder);
        when(orderStatusService.getOrderStatusHistory(testOrder)).thenReturn(historyDTOs);

        // Act
        ResponseEntity<List<OrderStatusHistoryDTO>> response = orderController.getOrderStatusHistory(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
    }
}