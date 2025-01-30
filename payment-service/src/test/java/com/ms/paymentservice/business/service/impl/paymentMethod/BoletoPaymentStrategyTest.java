package com.ms.paymentservice.business.service.impl.paymentMethod;

import com.ms.paymentservice.domain.model.dto.OrderDTO;
import com.ms.paymentservice.domain.model.entity.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BoletoPaymentStrategyTest {

    private BoletoPaymentStrategy strategy;
    private OrderDTO testOrder;

    @BeforeEach
    void setUp() {
        strategy = new BoletoPaymentStrategy();
        
        testOrder = new OrderDTO();
        testOrder.setId(1L);
        testOrder.setCustomerEmail("test@example.com");
        testOrder.setTotalAmount(new BigDecimal("100.00"));
    }

    @Test
    void processPayment_Success() {
        // Act
        Payment result = strategy.processPayment(testOrder);

        // Assert
        assertNotNull(result);
        assertEquals(testOrder.getId(), result.getOrderId());
        assertEquals(testOrder.getCustomerEmail(), result.getCustomerEmail());
        assertEquals(testOrder.getTotalAmount(), result.getAmount());
        assertEquals("BOLETO", result.getPaymentMethod());
        assertEquals("PENDING", result.getStatus());
        assertTrue(result.getPaymentLink().contains(testOrder.getId().toString()));
    }
}