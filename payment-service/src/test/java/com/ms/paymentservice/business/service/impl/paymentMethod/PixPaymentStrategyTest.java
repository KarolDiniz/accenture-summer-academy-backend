package com.ms.paymentservice.business.service.impl.paymentMethod;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Spy;
import org.mockito.InjectMocks;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ms.paymentservice.domain.model.dto.OrderDTO;
import com.ms.paymentservice.domain.model.entity.Payment;

@ExtendWith(MockitoExtension.class)
class PixPaymentStrategyTest {

    @Spy
    @InjectMocks
    private PixPaymentStrategy paymentStrategy;
    
    private OrderDTO order;

    @BeforeEach
    void setUp() {
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
        assertEquals("PIX", payment.getPaymentMethod());
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
    @DisplayName("Should maintain consistent success rate around 90%")
    void shouldMaintainExpectedSuccessRate() {
        int iterations = 1000;
        List<String> statuses = new ArrayList<>();
        
        for (int i = 0; i < iterations; i++) {
            Payment payment = paymentStrategy.processPayment(order);
            statuses.add(payment.getStatus());
        }
        
        long approvedCount = statuses.stream().filter(status -> status.equals("APPROVED")).count();
        double successRate = (double) approvedCount / iterations;
        
        assertTrue(successRate > 0.85 && successRate < 0.95, 
            "Success rate " + successRate + " should be approximately 0.9");
    }

    @Test
    @DisplayName("Multiple payments should have independent simulation results")
    void multiplePaymentsShouldHaveIndependentResults() {
        int payments = 100;
        List<String> statuses = new ArrayList<>();

        for (int i = 0; i < payments; i++) {
            Payment payment = paymentStrategy.processPayment(order);
            statuses.add(payment.getStatus());
        }

        boolean hasApproved = statuses.contains("APPROVED");
        boolean hasFailed = statuses.contains("FAILED");
        
        assertTrue(hasApproved && hasFailed, 
            "Multiple payments should result in both APPROVED and FAILED statuses");
    }

    @Test
    @DisplayName("Should process consecutive payments independently")
    void shouldProcessConsecutivePaymentsIndependently() {
        Payment payment1 = paymentStrategy.processPayment(order);
        Payment payment2 = paymentStrategy.processPayment(order);
        Payment payment3 = paymentStrategy.processPayment(order);

        assertNotNull(payment1.getStatus());
        assertNotNull(payment2.getStatus());
        assertNotNull(payment3.getStatus());
        
        assertTrue(
            !payment1.getStatus().equals(payment2.getStatus()) ||
            !payment2.getStatus().equals(payment3.getStatus()) ||
            !payment1.getStatus().equals(payment3.getStatus()),
            "At least some payments should have different statuses due to random simulation"
        );
    }
}