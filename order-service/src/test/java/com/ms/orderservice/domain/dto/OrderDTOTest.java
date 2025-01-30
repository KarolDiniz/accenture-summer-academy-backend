package com.ms.orderservice.domain.dto;

import com.ms.orderservice.domain.entity.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class OrderDTOTest {

    @Test
    void testOrderDTO() {
        // Arrange
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setCustomerEmail("test@test.com");
        orderDTO.setTotalAmount(new BigDecimal("100.00"));
        orderDTO.setCreatedAt(LocalDateTime.now());
        orderDTO.setStatus(OrderStatus.PENDING);
        orderDTO.setPaymentMethod("CREDIT_CARD");

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
        historyDTO.setChangeDate(LocalDateTime.now());
        orderDTO.setOrderStatusHistory(Collections.singletonList(historyDTO));

        // Act & Assert
        assertNotNull(orderDTO);
        assertEquals(1L, orderDTO.getId());
        assertEquals("test@test.com", orderDTO.getCustomerEmail());
        assertEquals(new BigDecimal("100.00"), orderDTO.getTotalAmount());
        assertEquals(OrderStatus.PENDING, orderDTO.getStatus());
        assertEquals("CREDIT_CARD", orderDTO.getPaymentMethod());
        assertEquals(1, orderDTO.getItems().size());
        assertEquals(1, orderDTO.getOrderStatusHistory().size());
    }
}