package com.ms.orderservice.domain.dto;

import com.ms.orderservice.domain.entity.OrderStatus;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class OrderStatusHistoryDTOTest {

    @Test
    void testOrderStatusHistoryDTO() {
        // Arrange
        OrderStatusHistoryDTO historyDTO = new OrderStatusHistoryDTO();
        historyDTO.setId(1L);
        historyDTO.setPreviousStatus(OrderStatus.PENDING);
        historyDTO.setCurrentStatus(OrderStatus.CONFIRMED);
        historyDTO.setChangeDate(LocalDateTime.now());

        // Act & Assert
        assertNotNull(historyDTO);
        assertEquals(1L, historyDTO.getId());
        assertEquals(OrderStatus.PENDING, historyDTO.getPreviousStatus());
        assertEquals(OrderStatus.CONFIRMED, historyDTO.getCurrentStatus());
        assertNotNull(historyDTO.getChangeDate());
    }
}