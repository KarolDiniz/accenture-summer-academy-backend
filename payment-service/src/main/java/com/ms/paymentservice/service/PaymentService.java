package com.ms.paymentservice.service;

import com.ms.paymentservice.model.Payment;
import com.ms.paymentservice.model.PaymentMethod;
import com.ms.paymentservice.model.dto.OrderDTO;
import com.ms.paymentservice.model.dto.PaymentDTO;

import java.math.BigDecimal;

import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.ms.paymentservice.repository.PaymentRepository;
import com.ms.paymentservice.service.impl.PaymentStrategyFactory;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final RabbitTemplate rabbitTemplate;
    private final Logger log = LoggerFactory.getLogger(PaymentService.class);
    private final PaymentRepository paymentRepository;
    private final PaymentStrategyFactory paymentStrategyFactory;
    private final ModelMapper modelMapper;

    public PaymentDTO processPayment(OrderDTO order) {
        
        log.info("Processing payment for the order: {}", order);

        // Obter o método de pagamento escolhido
        if (order.getPaymentMethod() == null || order.getPaymentMethod().isEmpty()) {
            throw new IllegalArgumentException("Payment method must not be null or empty");
        }
        PaymentMethod paymentMethod;
        try {
            paymentMethod = PaymentMethod.valueOf(order.getPaymentMethod().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("No strategy found for payment method: " + order.getPaymentMethod(), ex);
        }

        // Check if the total amount is valid
        if (order.getTotalAmount() == null || order.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Payment amount must be greater than zero");
        }

        // Obter a estratégia correta
        PaymentStrategy paymentStrategy = paymentStrategyFactory.getStrategy(paymentMethod);

        // Processar o pagamento usando a estratégia selecionada
        Payment payment = paymentStrategy.processPayment(order);

        // Salvar o pagamento no banco de dados
        Payment savedPayment = paymentRepository.save(payment);

        // Mapear o objeto Payment para PaymentDTO
        PaymentDTO paymentDTO = modelMapper.map(savedPayment, PaymentDTO.class);

        // Enviar o evento de pagamento processado
        rabbitTemplate.convertAndSend(
                "payment.exchange",
                "payment.routingkey",
                savedPayment
        );

        log.info("Payment result sent successfully");

        return paymentDTO;
    }
 
}
