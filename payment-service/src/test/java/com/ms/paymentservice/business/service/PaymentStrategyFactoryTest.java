package com.ms.paymentservice.business.service;

import com.ms.paymentservice.domain.model.entity.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentStrategyFactoryTest {

    private PaymentStrategyFactory factory;

    @Mock
    private PaymentStrategy mockStrategy;

    @BeforeEach
    void setUp() {
        // Inicializa os mocks
        MockitoAnnotations.openMocks(this);

        // Cria o mapa de estratégias
        Map<PaymentMethod, PaymentStrategy> strategies = new HashMap<>();
        strategies.put(PaymentMethod.CREDIT_CARD, mockStrategy);

        // Cria a fábrica com o mapa de estratégias
        factory = new PaymentStrategyFactory(strategies);
    }

    @Test
    void getStrategy_Success() {
        // Act
        PaymentStrategy strategy = factory.getStrategy(PaymentMethod.CREDIT_CARD);

        // Assert
        assertNotNull(strategy);
        assertEquals(mockStrategy, strategy); // Verifica se a estratégia retornada é o mock
    }

    @Test
    void getStrategy_InvalidMethod() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
            () -> factory.getStrategy(PaymentMethod.PIX));
    }
}