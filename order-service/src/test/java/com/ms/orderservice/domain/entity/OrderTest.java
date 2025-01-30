package com.ms.orderservice.domain.entity;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;

class OrderTest {

    @Test
    void testOrderCreation() {
        // Arrange
        Order order = new Order();
        order.setCustomerEmail("test@test.com");
        order.setTotalAmount(new BigDecimal("100.00"));
        order.setStatus(OrderStatus.PENDING);

        OrderItem orderItem = new OrderItem();
        orderItem.setSku("TEST-SKU");
        orderItem.setQuantity(1);
        orderItem.setPrice(new BigDecimal("100.00"));
        order.setItems(Collections.singletonList(orderItem));

        // Act & Assert
        assertNotNull(order);
        assertEquals("test@test.com", order.getCustomerEmail());
        assertEquals(OrderStatus.PENDING, order.getStatus());
        assertEquals(1, order.getItems().size());
    }
}