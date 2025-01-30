package com.ms.orderservice.domain.entity;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class OrderItemTest {

    @Test
    void OrderItemCreationTest() {
        // Arrange
        OrderItem orderItem = new OrderItem();
        orderItem.setSku("TEST-SKU");
        orderItem.setQuantity(1);
        orderItem.setPrice(new BigDecimal("100.00"));

        // Act & Assert
        assertNotNull(orderItem);
        assertEquals("TEST-SKU", orderItem.getSku());
        assertEquals(1, orderItem.getQuantity());
        assertEquals(new BigDecimal("100.00"), orderItem.getPrice());
    }
}