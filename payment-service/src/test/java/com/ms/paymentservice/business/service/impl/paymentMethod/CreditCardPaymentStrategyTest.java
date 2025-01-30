package com.ms.paymentservice.business.service.impl.paymentMethod;

import com.ms.paymentservice.domain.model.dto.OrderDTO;
import com.ms.paymentservice.domain.model.entity.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CreditCardPaymentStrategyTest {

    @InjectMocks
    private CreditCardPaymentStrategy creditCardPaymentStrategy;

    private OrderDTO orderDTO;

    @BeforeEach
    void setUp() {
        orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setCustomerEmail("test@example.com");
        orderDTO.setTotalAmount(new BigDecimal("100.00"));
    }

    @Test
    void processPayment_ShouldCreatePaymentWithCorrectData() {
        // Act
        Payment payment = creditCardPaymentStrategy.processPayment(orderDTO);

        // Assert
        assertNotNull(payment);
        assertEquals(orderDTO.getId(), payment.getOrderId());
        assertEquals(orderDTO.getCustomerEmail(), payment.getCustomerEmail());
        assertEquals(orderDTO.getTotalAmount(), payment.getAmount());
        assertEquals("CREDIT_CARD", payment.getPaymentMethod());
        assertNotNull(payment.getPaymentDate());
        assertTrue(payment.getPaymentDate().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertTrue(payment.getPaymentDate().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    void processPayment_ShouldSetStatusToApprovedOrFailed() {
        // Act
        Payment payment = creditCardPaymentStrategy.processPayment(orderDTO);

        // Assert
        String status = payment.getStatus();
        assertTrue("APPROVED".equals(status) || "FAILED".equals(status));
    }

    @Test
    void processPayment_MultipleCalls_ShouldCreateDifferentPayments() throws InterruptedException {
        // Act
        Payment payment1 = creditCardPaymentStrategy.processPayment(orderDTO);
        Thread.sleep(1); // Add a small delay to ensure different timestamps
        Payment payment2 = creditCardPaymentStrategy.processPayment(orderDTO);

        // Assert
        assertNotNull(payment1.getPaymentDate());
        assertNotNull(payment2.getPaymentDate());
        assertTrue(payment1.getPaymentDate().isBefore(payment2.getPaymentDate()));
    }
}