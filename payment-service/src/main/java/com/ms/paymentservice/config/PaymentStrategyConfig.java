package com.ms.paymentservice.config;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ms.paymentservice.model.PaymentMethod;
import com.ms.paymentservice.service.PaymentStrategy;
import com.ms.paymentservice.service.impl.BoletoPaymentStrategy;
import com.ms.paymentservice.service.impl.CreditCardPaymentStrategy;

@Configuration
public class PaymentStrategyConfig {

    @Bean
    public Map<PaymentMethod, PaymentStrategy> paymentStrategies(
            CreditCardPaymentStrategy creditCardPaymentStrategy,
            BoletoPaymentStrategy boletoPaymentStrategy
    ) {
        return Map.of(
            PaymentMethod.CREDIT_CARD, creditCardPaymentStrategy,
            PaymentMethod.BOLETO, boletoPaymentStrategy
        );
    }
}
