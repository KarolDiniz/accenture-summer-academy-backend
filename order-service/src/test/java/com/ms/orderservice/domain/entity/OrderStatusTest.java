package com.ms.orderservice.domain.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrderStatusTest {

    @Test
    void testValidTransitions() {
        assertTrue(OrderStatus.PENDING.canTransitionTo(OrderStatus.CONFIRMED));
        assertTrue(OrderStatus.PENDING.canTransitionTo(OrderStatus.CANCELLED));
        assertTrue(OrderStatus.CONFIRMED.canTransitionTo(OrderStatus.SHIPPING));
        assertTrue(OrderStatus.SHIPPING.canTransitionTo(OrderStatus.DELIVERED));
    }

    @Test
    void testInvalidTransitions() {
        assertFalse(OrderStatus.PENDING.canTransitionTo(OrderStatus.DELIVERED));
        assertFalse(OrderStatus.CONFIRMED.canTransitionTo(OrderStatus.DELIVERED));
        assertFalse(OrderStatus.SHIPPING.canTransitionTo(OrderStatus.CANCELLED));
        assertFalse(OrderStatus.DELIVERED.canTransitionTo(OrderStatus.PENDING));
        assertFalse(OrderStatus.CANCELLED.canTransitionTo(OrderStatus.CONFIRMED));
    }

    @Test
    void testTerminalStates() {
        assertTrue(OrderStatus.DELIVERED.isTerminalState());
        assertTrue(OrderStatus.CANCELLED.isTerminalState());
        assertFalse(OrderStatus.PENDING.isTerminalState());
        assertFalse(OrderStatus.CONFIRMED.isTerminalState());
        assertFalse(OrderStatus.SHIPPING.isTerminalState());
    }

    @Test
    void testFromStringValidValues() {
        assertEquals(OrderStatus.PENDING, OrderStatus.fromString("PENDING"));
        assertEquals(OrderStatus.CONFIRMED, OrderStatus.fromString("CONFIRMED"));
        assertEquals(OrderStatus.SHIPPING, OrderStatus.fromString("SHIPPING"));
        assertEquals(OrderStatus.DELIVERED, OrderStatus.fromString("DELIVERED"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.fromString("CANCELLED"));
    }

    @Test
    void testFromStringCaseInsensitive() {
        assertEquals(OrderStatus.PENDING, OrderStatus.fromString("pending"));
        assertEquals(OrderStatus.CONFIRMED, OrderStatus.fromString("confirmed"));
        assertEquals(OrderStatus.SHIPPING, OrderStatus.fromString("shipping"));
        assertEquals(OrderStatus.DELIVERED, OrderStatus.fromString("delivered"));
        assertEquals(OrderStatus.CANCELLED, OrderStatus.fromString("cancelled"));
    }

    @Test
    void testFromStringInvalidValue() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> 
            OrderStatus.fromString("INVALID_STATUS"));

        String expectedMessage = "Invalid order status: INVALID_STATUS";
        assertTrue(exception.getMessage().contains(expectedMessage));
    }
}
