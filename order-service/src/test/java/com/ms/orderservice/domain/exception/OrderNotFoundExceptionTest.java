package com.ms.orderservice.domain.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderNotFoundExceptionTest {

    @Test
    void testOrderNotFoundExceptionWithOrderId() {
        // Arrange
        Long orderId = 1L;

        // Act
        OrderNotFoundException exception = new OrderNotFoundException(orderId);

        // Assert
        assertEquals("Order not found with id: 1", exception.getMessage());
    }

    @Test
    void testOrderNotFoundExceptionWithCustomMessage() {
        // Arrange
        String customMessage = "Custom error message";

        // Act
        OrderNotFoundException exception = new OrderNotFoundException(customMessage);

        // Assert
        assertEquals(customMessage, exception.getMessage());
    }
}