package com.ms.paymentservice.service;

import com.ms.paymentservice.model.Payment;
import com.ms.paymentservice.model.dto.OrderDTO;
import com.ms.paymentservice.model.dto.PaymentDTO;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ms.paymentservice.repository.PaymentRepository;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class PaymentService {


    private final RabbitTemplate rabbitTemplate;
    private final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository paymentRepository;
    private final ModelMapper modelMapper;

    public PaymentDTO processPayment(OrderDTO order) {

        log.info("Processing payment for order: {}", order);
        // Criar registro de pagamento
        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setCustomerEmail(order.getCustomerEmail());
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setPaymentMethod("CREDIT_CARD"); // simular pagamento com cartão de crédito

        // Simular processamento de pagamento
        boolean paymentSuccess = simulatePaymentProcessing();

        // Atualizar status do pagamento
        payment.setStatus(paymentSuccess ? "APPROVED" : "FAILED");

        // Salvar registro de pagamento
        Payment savepayment = paymentRepository.save(payment);

        PaymentDTO paymentDTO = modelMapper.map(savepayment, PaymentDTO.class);

        // Publicar evento de pagamento processado
        rabbitTemplate.convertAndSend(
                "payment.exchange",
                "payment.routingkey",
                payment
        );
        log.info("Payment result sent successfully");

        return paymentDTO;
    }

    private boolean simulatePaymentProcessing() {
        return Math.random() < 0.8;
    }
}
