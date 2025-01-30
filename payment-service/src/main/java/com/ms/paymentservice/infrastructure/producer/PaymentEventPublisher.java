package com.ms.paymentservice.infrastructure.producer;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.ms.paymentservice.domain.model.dto.PaymentDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentEventPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final Logger log = LoggerFactory.getLogger(PaymentEventPublisher.class);

    public void publishPaymentProcessed(PaymentDTO paymentDTO) {
        rabbitTemplate.convertAndSend(
                "payment.exchange",
                "payment.routingkey",
                paymentDTO
        );
        log.info("Published payment processed event: {}", paymentDTO);
    }
}
