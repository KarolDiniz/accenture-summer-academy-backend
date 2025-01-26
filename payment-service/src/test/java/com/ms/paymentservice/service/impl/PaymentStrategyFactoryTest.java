package com.ms.paymentservice.service.impl;

import com.ms.paymentservice.model.PaymentMethod;
import com.ms.paymentservice.service.PaymentStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentStrategyFactoryTest {

    private PaymentStrategyFactory factory;

    @BeforeEach
    void setUp() {
        Map<PaymentMethod, PaymentStrategy> strategies = Map.of(
                PaymentMethod.CREDIT_CARD, mock(PaymentStrategy.class),
                PaymentMethod.BOLETO, mock(PaymentStrategy.class)
        );

        factory = new PaymentStrategyFactory(strategies);
    }

    @Test
    void shouldReturnCorrectStrategyForCreditCard() {
        // Act
        PaymentStrategy strategy = factory.getStrategy(PaymentMethod.CREDIT_CARD);

        // Assert
        assertNotNull(strategy);
    }

    @Test
    void shouldThrowExceptionForInvalidPaymentMethod() {
        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                factory.getStrategy(PaymentMethod.PIX));
        assertEquals("No strategy found for payment method: PIX", exception.getMessage());
    }

    @Test
    void getStrategy_withValidMethod_shouldReturnStrategy() {
        PaymentStrategy strategy = factory.getStrategy(PaymentMethod.CREDIT_CARD);
        assertNotNull(strategy);
    }

    @Test
    void getStrategy_withInvalidMethod_shouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> factory.getStrategy(PaymentMethod.PIX));
    }

    @Test
    void getStrategy_shouldThrowExceptionForInvalidMethod() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                factory.getStrategy(PaymentMethod.PIX)
        );
        assertEquals("No strategy found for payment method: PIX", exception.getMessage());
    }
}
