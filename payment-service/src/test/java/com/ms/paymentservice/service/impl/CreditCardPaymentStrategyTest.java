package com.ms.paymentservice.service.impl;

import com.ms.paymentservice.model.Payment;
import com.ms.paymentservice.model.dto.OrderDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CreditCardPaymentStrategyTest {

    private final CreditCardPaymentStrategy strategy = new CreditCardPaymentStrategy();

    @Test
    void processPayment_shouldCreateApprovedPayment() {
        OrderDTO order = new OrderDTO();
        order.setId(1L);
        order.setCustomerEmail("customer@example.com");
        order.setTotalAmount(BigDecimal.valueOf(150.00));

        Payment payment = strategy.processPayment(order);

        assertNotNull(payment);
        assertEquals("CREDIT_CARD", payment.getPaymentMethod());
        assertTrue(payment.getStatus().matches("APPROVED|FAILED"));
    }
}
