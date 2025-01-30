package com.ms.orderservice.business.service.impl;

import com.ms.orderservice.domain.dto.OrderStatusHistoryDTO;
import com.ms.orderservice.domain.entity.Order;
import com.ms.orderservice.domain.entity.OrderStatus;
import com.ms.orderservice.domain.entity.OrderStatusHistory;
import com.ms.orderservice.domain.exception.InvalidStatusTransitionException;
import com.ms.orderservice.domain.repository.OrderStatusHistoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderStatusServiceImplTest {

    @Mock
    private OrderStatusHistoryRepository orderStatusHistoryRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private OrderStatusServiceImpl orderStatusService;

    private Order testOrder;
    private OrderStatusHistory testHistory;
    private OrderStatusHistoryDTO testHistoryDTO;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setStatus(OrderStatus.PENDING);

        testHistory = new OrderStatusHistory(testOrder, OrderStatus.PENDING, OrderStatus.CONFIRMED);
        testHistory.setChangeDate(LocalDateTime.now());

        testHistoryDTO = new OrderStatusHistoryDTO();
        testHistoryDTO.setId(1L);
        testHistoryDTO.setPreviousStatus(OrderStatus.PENDING);
        testHistoryDTO.setCurrentStatus(OrderStatus.CONFIRMED);
        testHistoryDTO.setChangeDate(LocalDateTime.now());
    }

    @Test
    void validateAndCreateStatusHistory_ValidTransition() {
        // Arrange
        when(orderStatusHistoryRepository.save(any(OrderStatusHistory.class))).thenReturn(testHistory);

        // Act
        orderStatusService.validateAndCreateStatusHistory(testOrder, OrderStatus.CONFIRMED);

        // Assert
        verify(orderStatusHistoryRepository).save(any(OrderStatusHistory.class));
    }

    @Test
    void validateAndCreateStatusHistory_InvalidTransition() {
        // Arrange
        testOrder.setStatus(OrderStatus.PENDING);

        // Act & Assert
        assertThrows(InvalidStatusTransitionException.class,
                () -> orderStatusService.validateAndCreateStatusHistory(testOrder, OrderStatus.DELIVERED));

        verify(orderStatusHistoryRepository, never()).save(any());
    }

    @Test
    void validateAndCreateStatusHistory_SameStatus() {
        // Arrange
        testOrder.setStatus(OrderStatus.PENDING);

        // Act
        orderStatusService.validateAndCreateStatusHistory(testOrder, OrderStatus.PENDING);

        // Assert
        verify(orderStatusHistoryRepository, never()).save(any());
    }

    @Test
    void getOrderStatusHistory_WithHistory() {
        // Arrange
        List<OrderStatusHistory> historyList = Arrays.asList(testHistory);
        when(orderStatusHistoryRepository.findByOrderOrderByChangeDateDesc(testOrder)).thenReturn(historyList);
        when(modelMapper.map(testHistory, OrderStatusHistoryDTO.class)).thenReturn(testHistoryDTO);

        // Act
        List<OrderStatusHistoryDTO> result = orderStatusService.getOrderStatusHistory(testOrder);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testHistoryDTO.getCurrentStatus(), result.get(0).getCurrentStatus());
        verify(orderStatusHistoryRepository).findByOrderOrderByChangeDateDesc(testOrder);
        verify(modelMapper).map(testHistory, OrderStatusHistoryDTO.class);
    }

    @Test
    void getOrderStatusHistory_EmptyHistory() {
        // Arrange
        when(orderStatusHistoryRepository.findByOrderOrderByChangeDateDesc(testOrder)).thenReturn(Collections.emptyList());

        // Act
        List<OrderStatusHistoryDTO> result = orderStatusService.getOrderStatusHistory(testOrder);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(orderStatusHistoryRepository).findByOrderOrderByChangeDateDesc(testOrder);
        verify(modelMapper, never()).map(any(), any());
    }
}