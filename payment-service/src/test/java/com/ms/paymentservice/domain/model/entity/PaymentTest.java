package com.ms.paymentservice.domain.model.entity;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class PaymentTest {

    @Test
    void shouldCreatePaymentAndVerifyValues() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setOrderId(100L);
        payment.setCustomerEmail("customer@example.com");
        payment.setAmount(BigDecimal.valueOf(200.50));
        payment.setPaymentDate(LocalDateTime.of(2025, 1, 29, 12, 0));
        payment.setStatus("APPROVED");
        payment.setPaymentMethod("CREDIT_CARD");
        payment.setPaymentLink("http://payment.link/123");

        assertEquals(1L, payment.getId());
        assertEquals(100L, payment.getOrderId());
        assertEquals("customer@example.com", payment.getCustomerEmail());
        assertEquals(BigDecimal.valueOf(200.50), payment.getAmount());
        assertEquals(LocalDateTime.of(2025, 1, 29, 12, 0), payment.getPaymentDate());
        assertEquals("APPROVED", payment.getStatus());
        assertEquals("CREDIT_CARD", payment.getPaymentMethod());
        assertEquals("http://payment.link/123", payment.getPaymentLink());
    }

    @Test
    void shouldVerifyEqualsAndHashCode() {
        Payment payment1 = new Payment();
        payment1.setId(1L);
        payment1.setOrderId(100L);
        payment1.setCustomerEmail("customer@example.com");

        Payment payment2 = new Payment();
        payment2.setId(1L);
        payment2.setOrderId(100L);
        payment2.setCustomerEmail("customer@example.com");

        assertEquals(payment1, payment2);
        assertEquals(payment1.hashCode(), payment2.hashCode());

        payment2.setCustomerEmail("other@example.com");
        assertNotEquals(payment1, payment2);
    }

    @Test
    void shouldVerifyToString() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setStatus("APPROVED");

        String result = payment.toString();
        assertTrue(result.contains("1"));
        assertTrue(result.contains("APPROVED"));
    }
}
