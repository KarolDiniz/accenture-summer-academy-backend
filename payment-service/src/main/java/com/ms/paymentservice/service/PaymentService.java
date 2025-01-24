package com.ms.paymentservice.service;

import com.ms.paymentservice.model.OrderDTO;
import com.ms.paymentservice.model.Payment;
import com.ms.paymentservice.model.PaymentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RabbitTemplate rabbitTemplate;
    private final Logger log = LoggerFactory.getLogger(PaymentService.class);

    public void processPayment(OrderDTO order) {
        log.info("Processing payment for order: {}", order);
        // Criar registro de pagamento
        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setCustomerEmail(order.getCustomerEmail());
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod("CREDIT_CARD");

        // Simular processamento de pagamento
        boolean paymentSuccess = simulatePaymentProcessing();

        // Atualizar status do pagamento
        payment.setStatus(paymentSuccess ? "APPROVED" : "FAILED");

        // Criar DTO para envio
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setOrderId(payment.getOrderId());
        paymentDTO.setStatus(payment.getStatus());

        log.info("Sending payment result: {}", paymentDTO);
        // Publicar evento de pagamento processado
        rabbitTemplate.convertAndSend(
                "payment.exchange",
                "payment.routingkey",
                paymentDTO
        );
        log.info("Payment result sent successfully");
    }

    private boolean simulatePaymentProcessing() {
        return Math.random() < 0.8;
    }
}
