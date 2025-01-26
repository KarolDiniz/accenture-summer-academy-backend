package com.ms.paymentservice.service.impl;

import com.ms.paymentservice.model.PaymentMethod;
import com.ms.paymentservice.service.PaymentStrategy;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentStrategyFactory {

    private final Map<PaymentMethod, PaymentStrategy> strategies;

    public PaymentStrategyFactory(Map<PaymentMethod, PaymentStrategy> strategies) {
        this.strategies = strategies;
    }

    public PaymentStrategy getStrategy(PaymentMethod paymentMethod) {
        PaymentStrategy strategy = strategies.get(paymentMethod);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for payment method: " + paymentMethod);
        }
        return strategy;
    }
}
