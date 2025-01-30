package com.ms.paymentservice.business.service.impl.paymentMethod;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.ms.paymentservice.domain.model.dto.OrderDTO;
import com.ms.paymentservice.domain.model.entity.Payment;

class DebitCardPaymentStrategyTest {

    private DebitCardPaymentStrategy paymentStrategy;
    private OrderDTO order;

    @BeforeEach
    void setUp() {
        paymentStrategy = new DebitCardPaymentStrategy();
        order = new OrderDTO();
        order.setId(1L);
        order.setCustomerEmail("customer@example.com");
        order.setTotalAmount(BigDecimal.valueOf(100.0));
    }

    @Test
    @DisplayName("Should process payment successfully with all fields correctly set")
    void shouldProcessPaymentSuccessfully() {
        Payment payment = paymentStrategy.processPayment(order);

        assertNotNull(payment);
        assertEquals(order.getId(), payment.getOrderId());
        assertEquals(order.getCustomerEmail(), payment.getCustomerEmail());
        assertEquals(order.getTotalAmount(), payment.getAmount());
        assertEquals("DEBIT_CARD", payment.getPaymentMethod());
        assertNotNull(payment.getPaymentDate());
        assertTrue(payment.getStatus().equals("APPROVED") || payment.getStatus().equals("FAILED"));
    }

    @Test
    @DisplayName("Should handle zero amount payment")
    void shouldHandleZeroAmountPayment() {
        order.setTotalAmount(BigDecimal.ZERO);
        
        Payment payment = paymentStrategy.processPayment(order);
        
        assertNotNull(payment);
        assertEquals(BigDecimal.ZERO, payment.getAmount());
        assertTrue(payment.getStatus().equals("APPROVED") || payment.getStatus().equals("FAILED"));
    }

    @Test
    @DisplayName("Should handle null email")
    void shouldHandleNullEmail() {
        order.setCustomerEmail(null);
        
        Payment payment = paymentStrategy.processPayment(order);
        
        assertNotNull(payment);
        assertNull(payment.getCustomerEmail());
    }

    @Test
    @DisplayName("Should create payment with current timestamp")
    void shouldCreatePaymentWithCurrentTimestamp() {
        LocalDateTime before = LocalDateTime.now();
        Payment payment = paymentStrategy.processPayment(order);
        LocalDateTime after = LocalDateTime.now();
        
        assertNotNull(payment.getPaymentDate());
        assertTrue(payment.getPaymentDate().isEqual(before) || payment.getPaymentDate().isAfter(before));
        assertTrue(payment.getPaymentDate().isEqual(after) || payment.getPaymentDate().isBefore(after));
    }

    @Test
    @DisplayName("Should handle multiple payments for same order")
    void shouldHandleMultiplePaymentsForSameOrder() throws InterruptedException {
        Payment payment1 = paymentStrategy.processPayment(order);
        Thread.sleep(1); // Ensure different timestamps
        Payment payment2 = paymentStrategy.processPayment(order);
        
        assertNotNull(payment1);
        assertNotNull(payment2);
        assertEquals(payment1.getOrderId(), payment2.getOrderId());
        assertTrue(payment1.getPaymentDate().isBefore(payment2.getPaymentDate()));
    }

    @Test
    @DisplayName("Should handle large amount payment")
    void shouldHandleLargeAmountPayment() {
        order.setTotalAmount(new BigDecimal("999999999.99"));
        
        Payment payment = paymentStrategy.processPayment(order);
        
        assertNotNull(payment);
        assertEquals(new BigDecimal("999999999.99"), payment.getAmount());
        assertTrue(payment.getStatus().equals("APPROVED") || payment.getStatus().equals("FAILED"));
    }

    @Test
    @DisplayName("Should handle negative amount payment")
    void shouldHandleNegativeAmountPayment() {
        order.setTotalAmount(new BigDecimal("-100.00"));
        
        Payment payment = paymentStrategy.processPayment(order);
        
        assertNotNull(payment);
        assertEquals(new BigDecimal("-100.00"), payment.getAmount());
        assertTrue(payment.getStatus().equals("APPROVED") || payment.getStatus().equals("FAILED"));
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, 1L, Long.MAX_VALUE})
    @DisplayName("Should handle different order IDs")
    void shouldHandleDifferentOrderIds(Long orderId) {
        order.setId(orderId);
        
        Payment payment = paymentStrategy.processPayment(order);
        
        assertNotNull(payment);
        assertEquals(orderId, payment.getOrderId());
    }

    @Test
    @DisplayName("Should handle empty email")
    void shouldHandleEmptyEmail() {
        order.setCustomerEmail("");
        
        Payment payment = paymentStrategy.processPayment(order);
        
        assertNotNull(payment);
        assertEquals("", payment.getCustomerEmail());
    }

    @Test
    @DisplayName("Should maintain consistent payment method")
    void shouldMaintainConsistentPaymentMethod() {
        Payment payment1 = paymentStrategy.processPayment(order);
        Payment payment2 = paymentStrategy.processPayment(order);
        Payment payment3 = paymentStrategy.processPayment(order);
        
        assertEquals("DEBIT_CARD", payment1.getPaymentMethod());
        assertEquals("DEBIT_CARD", payment2.getPaymentMethod());
        assertEquals("DEBIT_CARD", payment3.getPaymentMethod());
    }
}