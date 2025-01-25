package com.ms.paymentservice.service.impl;

import com.ms.paymentservice.model.Payment;
import com.ms.paymentservice.model.dto.OrderDTO;
import com.ms.paymentservice.service.PaymentStrategy;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class CreditCardPaymentStrategy implements PaymentStrategy {

    @Override
    public Payment processPayment(OrderDTO order) {

        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setCustomerEmail(order.getCustomerEmail());
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod("CREDIT_CARD");
        
        boolean paymentSuccess = simulatePaymentProcessing();
        payment.setStatus(paymentSuccess ? "APPROVED" : "FAILED");

        return payment;
    }
    private boolean simulatePaymentProcessing() {
        return Math.random() < 0.8;
    }
}
