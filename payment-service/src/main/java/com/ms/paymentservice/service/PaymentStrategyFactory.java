package com.ms.paymentservice.service;

import com.ms.paymentservice.model.entity.PaymentMethod;

import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentStrategyFactory {

    private final Map<PaymentMethod, PaymentStrategy> strategies;

    public PaymentStrategyFactory(Map<PaymentMethod, PaymentStrategy> strategies) {
        this.strategies = strategies;
    }

    public PaymentStrategy getStrategy(PaymentMethod paymentMethod) {
        
        if (!strategies.containsKey(paymentMethod)) {
            throw new IllegalArgumentException("No strategy found for payment method: " + paymentMethod);
        }
        return strategies.get(paymentMethod);
    }
}
