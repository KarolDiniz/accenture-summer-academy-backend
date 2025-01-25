package com.ms.paymentservice.service.impl;

import com.ms.paymentservice.model.Payment;
import com.ms.paymentservice.model.dto.OrderDTO;
import com.ms.paymentservice.service.PaymentStrategy;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class BoletoPaymentStrategy implements PaymentStrategy {
    
    @Override
    public Payment processPayment(OrderDTO order) {
        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setCustomerEmail(order.getCustomerEmail());
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod("BOLETO");

        // Gerar link ou código do boleto
        payment.setPaymentLink("https://paymentservice.com/gerar-boleto?orderId=" + order.getId());
        payment.setStatus("PENDING"); // Pagamento via boleto é inicialmente pendente

        return payment;
    }
}

