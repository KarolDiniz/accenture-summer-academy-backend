package com.ms.orderservice.domain.exception;

import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InsufficientStockExceptionTest {

    @Test
    void testInsufficientStockException() {
        // Arrange
        List<String> unavailableSkus = Arrays.asList("SKU1", "SKU2");

        // Act
        InsufficientStockException exception = new InsufficientStockException(unavailableSkus);

        // Assert
        assertEquals("Insufficient stock for products: SKU1, SKU2", exception.getMessage());
        assertEquals(unavailableSkus, exception.getUnavailableSkus());
    }
}