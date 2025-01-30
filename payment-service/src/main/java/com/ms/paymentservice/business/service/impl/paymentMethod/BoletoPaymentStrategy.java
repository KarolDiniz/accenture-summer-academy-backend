package com.ms.paymentservice.business.service.impl.paymentMethod;

import com.ms.paymentservice.business.service.PaymentStrategy;
import com.ms.paymentservice.domain.model.dto.OrderDTO;
import com.ms.paymentservice.domain.model.entity.Payment;

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
        
        payment.setStatus("PENDING");

        return payment;
    }
}

