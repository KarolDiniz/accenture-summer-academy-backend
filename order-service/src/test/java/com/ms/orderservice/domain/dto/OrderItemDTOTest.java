package com.ms.orderservice.domain.dto;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class OrderItemDTOTest {

    @Test
    void testOrderItemDTO() {
        // Arrange
        OrderItemDTO orderItemDTO = new OrderItemDTO();
        orderItemDTO.setId(1L);
        orderItemDTO.setProductId(1L);
        orderItemDTO.setSku("TEST-SKU");
        orderItemDTO.setQuantity(1);
        orderItemDTO.setPrice(new BigDecimal("100.00"));

        // Act & Assert
        assertNotNull(orderItemDTO);
        assertEquals(1L, orderItemDTO.getId());
        assertEquals(1L, orderItemDTO.getProductId());
        assertEquals("TEST-SKU", orderItemDTO.getSku());
        assertEquals(1, orderItemDTO.getQuantity());
        assertEquals(new BigDecimal("100.00"), orderItemDTO.getPrice());
    }
}