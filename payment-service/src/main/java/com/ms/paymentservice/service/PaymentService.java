package com.ms.paymentservice.service;

import com.ms.paymentservice.model.OrderDTO;
import com.ms.paymentservice.model.Payment;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentService {
    
    private final RabbitTemplate rabbitTemplate;

    public void processPayment(OrderDTO order) {
        // Criar registro de pagamento
        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setCustomerEmail(order.getCustomerEmail());
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod("CREDIT_CARD"); // Simulação

        // Simular processamento de pagamento
        boolean paymentSuccess = simulatePaymentProcessing();

        // Atualizar status do pagamento
        payment.setStatus(paymentSuccess ? "APPROVED" : "FAILED");

        // Publicar evento de pagamento processado
        rabbitTemplate.convertAndSend(
                "payment.exchange",
                "payment.routingkey",
                payment
        );
    }

    private boolean simulatePaymentProcessing() {
        // Simular processamento com 80% de chance de sucesso
        return Math.random() < 0.8;
    }
}
