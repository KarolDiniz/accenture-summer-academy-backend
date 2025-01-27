package com.ms.paymentservice.service.impl.paymentMethod;

import java.time.LocalDateTime;

import com.ms.paymentservice.model.dto.OrderDTO;
import com.ms.paymentservice.model.entity.Payment;
import com.ms.paymentservice.service.PaymentStrategy;

public class DebitCardPaymentStrategy implements PaymentStrategy {

    @Override
    public Payment processPayment(OrderDTO order) {
        
        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setCustomerEmail(order.getCustomerEmail());
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod("DEBIT_CARD");

        boolean paymentSuccess = simulatePaymentProcessing();
        payment.setStatus(paymentSuccess ? "APPROVED" : "FAILED");

        return payment;
    }

    private boolean simulatePaymentProcessing() {
        return Math.random() < 0.9;
    }
    
}
