package com.ms.paymentservice.infrastructure.config;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ms.paymentservice.business.service.PaymentStrategy;
import com.ms.paymentservice.business.service.impl.paymentMethod.BoletoPaymentStrategy;
import com.ms.paymentservice.business.service.impl.paymentMethod.CreditCardPaymentStrategy;
import com.ms.paymentservice.business.service.impl.paymentMethod.DebitCardPaymentStrategy;
import com.ms.paymentservice.business.service.impl.paymentMethod.PixPaymentStrategy;
import com.ms.paymentservice.domain.model.entity.PaymentMethod;

@Configuration
public class PaymentStrategyConfig {

    @Bean
    public Map<PaymentMethod, PaymentStrategy> paymentStrategies(
            CreditCardPaymentStrategy creditCardPaymentStrategy,
            BoletoPaymentStrategy boletoPaymentStrategy,
            PixPaymentStrategy pixPaymentStrategy,
            DebitCardPaymentStrategy debitCardPaymentStrategy
    ) {
        return Map.of(
            PaymentMethod.CREDIT_CARD, creditCardPaymentStrategy,
            PaymentMethod.BOLETO, boletoPaymentStrategy,
            PaymentMethod.PIX, pixPaymentStrategy,
            PaymentMethod.DEBIT_CARD, debitCardPaymentStrategy
        );
    }
}
