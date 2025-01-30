package com.ms.paymentservice.infrastructure.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ms.paymentservice.business.service.PaymentStrategy;
import com.ms.paymentservice.business.service.impl.paymentMethod.BoletoPaymentStrategy;
import com.ms.paymentservice.business.service.impl.paymentMethod.CreditCardPaymentStrategy;
import com.ms.paymentservice.business.service.impl.paymentMethod.DebitCardPaymentStrategy;
import com.ms.paymentservice.business.service.impl.paymentMethod.PixPaymentStrategy;
import com.ms.paymentservice.domain.model.entity.PaymentMethod;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

class PaymentStrategyConfigTest {

    private PaymentStrategyConfig config;

    private CreditCardPaymentStrategy creditCardPaymentStrategy;
    private BoletoPaymentStrategy boletoPaymentStrategy;
    private PixPaymentStrategy pixPaymentStrategy;
    private DebitCardPaymentStrategy debitCardPaymentStrategy;

    @BeforeEach
    void setUp() {
        creditCardPaymentStrategy = mock(CreditCardPaymentStrategy.class);
        boletoPaymentStrategy = mock(BoletoPaymentStrategy.class);
        pixPaymentStrategy = mock(PixPaymentStrategy.class);
        debitCardPaymentStrategy = mock(DebitCardPaymentStrategy.class);

        config = new PaymentStrategyConfig();
    }

    @Test
    void shouldReturnCorrectPaymentStrategiesMap() {
        Map<PaymentMethod, PaymentStrategy> strategies = config.paymentStrategies(
                creditCardPaymentStrategy,
                boletoPaymentStrategy,
                pixPaymentStrategy,
                debitCardPaymentStrategy
        );

        assertNotNull(strategies);
        assertEquals(4, strategies.size());

        assertSame(creditCardPaymentStrategy, strategies.get(PaymentMethod.CREDIT_CARD));
        assertSame(boletoPaymentStrategy, strategies.get(PaymentMethod.BOLETO));
        assertSame(pixPaymentStrategy, strategies.get(PaymentMethod.PIX));
        assertSame(debitCardPaymentStrategy, strategies.get(PaymentMethod.DEBIT_CARD));
    }
}
