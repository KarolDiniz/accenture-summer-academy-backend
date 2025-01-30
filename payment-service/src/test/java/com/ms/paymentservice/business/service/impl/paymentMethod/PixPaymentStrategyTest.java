package com.ms.paymentservice.business.service.impl.paymentMethod;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ms.paymentservice.domain.model.dto.OrderDTO;
import com.ms.paymentservice.domain.model.entity.Payment;

class PixPaymentStrategyTest {

    private PixPaymentStrategy paymentStrategy;

    @BeforeEach
    void setUp() {
        paymentStrategy = new PixPaymentStrategy();
    }

    @Test
    void shouldProcessPaymentSuccessfully() {
        OrderDTO order = new OrderDTO();
        order.setId(1L);
        order.setCustomerEmail("customer@example.com");
        order.setTotalAmount(BigDecimal.valueOf(100.0));

        Payment payment = paymentStrategy.processPayment(order);

        assertNotNull(payment);
        assertEquals(order.getId(), payment.getOrderId());
        assertEquals(order.getCustomerEmail(), payment.getCustomerEmail());
        assertEquals(order.getTotalAmount(), payment.getAmount());
        assertEquals("PIX", payment.getPaymentMethod());
        assertNotNull(payment.getPaymentDate());
        assertTrue(payment.getStatus().equals("APPROVED") || payment.getStatus().equals("FAILED"));
    }
}
