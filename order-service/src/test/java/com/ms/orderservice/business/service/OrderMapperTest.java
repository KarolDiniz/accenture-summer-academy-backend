package com.ms.orderservice.business.service;

import com.ms.orderservice.domain.dto.OrderDTO;
import com.ms.orderservice.domain.dto.OrderItemDTO;
import com.ms.orderservice.domain.dto.OrderStatusHistoryDTO;
import com.ms.orderservice.domain.entity.Order;
import com.ms.orderservice.domain.entity.OrderItem;
import com.ms.orderservice.domain.entity.OrderStatus;
import com.ms.orderservice.domain.entity.OrderStatusHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class OrderMapperTest {

    private OrderMapper orderMapper;
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        modelMapper = new ModelMapper();

        // Ignorar ambiguidades
        modelMapper.getConfiguration().setAmbiguityIgnored(true);

        // Evita ciclos entre Order e OrderStatusHistory
        modelMapper.typeMap(OrderStatusHistory.class, OrderStatusHistoryDTO.class).addMappings(mapper -> {
            mapper.skip(OrderStatusHistoryDTO::setId); // Apenas um exemplo, se necessário
        });

        modelMapper.typeMap(Order.class, OrderDTO.class).addMappings(mapper -> {
            mapper.skip(OrderDTO::setOrderStatusHistory); // Evita problemas de ciclo
        });

        orderMapper = new OrderMapper(modelMapper);
    }

    @Test
    void toDTO_Success() {
        // Arrange
        Order order = new Order();
        order.setId(1L);
        order.setCustomerEmail("test@test.com");
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(new BigDecimal("100.00"));
        order.setCreatedAt(LocalDateTime.now());

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setProductId(1L);
        orderItem.setSku("TEST-SKU");
        orderItem.setQuantity(1);
        orderItem.setPrice(new BigDecimal("100.00"));
        order.setItems(Collections.singletonList(orderItem));

        OrderStatusHistory history = new OrderStatusHistory(order, OrderStatus.PENDING, OrderStatus.CONFIRMED);
        order.setOrderStatusHistory(Collections.singletonList(history));

        // Act
        OrderDTO orderDTO = orderMapper.toDTO(order);

        // Assert
        assertNotNull(orderDTO);
        assertEquals(order.getId(), orderDTO.getId());
        assertEquals(order.getCustomerEmail(), orderDTO.getCustomerEmail());
        assertEquals(order.getStatus(), orderDTO.getStatus());
        assertEquals(order.getTotalAmount(), orderDTO.getTotalAmount());
        assertEquals(1, orderDTO.getItems().size());
        assertEquals(1, orderDTO.getOrderStatusHistory().size());
    }

    @Test
    void toEntity_Success() {
        // Arrange
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setCustomerEmail("test@test.com");
        orderDTO.setStatus(OrderStatus.PENDING);
        orderDTO.setTotalAmount(new BigDecimal("100.00"));
        orderDTO.setCreatedAt(LocalDateTime.now());

        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setId(1L);
        orderItemDTO.setProductId(1L);
        orderItemDTO.setSku("TEST-SKU");
        orderItemDTO.setQuantity(1);
        orderItemDTO.setPrice(new BigDecimal("100.00"));
        orderDTO.setItems(Collections.singletonList(orderItemDTO));

        OrderStatusHistoryDTO historyDTO = new OrderStatusHistoryDTO();
        historyDTO.setId(1L);
        historyDTO.setPreviousStatus(OrderStatus.PENDING);
        historyDTO.setCurrentStatus(OrderStatus.CONFIRMED);
        orderDTO.setOrderStatusHistory(Collections.singletonList(historyDTO));

        // Act
        Order order = orderMapper.toEntity(orderDTO);

        // Assert
        assertNotNull(order);
        assertEquals(orderDTO.getId(), order.getId());
        assertEquals(orderDTO.getCustomerEmail(), order.getCustomerEmail());
        assertEquals(orderDTO.getStatus(), order.getStatus());
        assertEquals(orderDTO.getTotalAmount(), order.getTotalAmount());
        assertEquals(1, order.getItems().size());
        assertEquals(1, order.getOrderStatusHistory().size());
    }
}