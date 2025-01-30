package com.ms.paymentservice.infrastructure.consumer;

import com.ms.paymentservice.business.service.PaymentService;
import com.ms.paymentservice.domain.model.dto.OrderDTO;
import com.ms.paymentservice.domain.model.exception.MessageSendFailedException;
import com.ms.paymentservice.domain.model.exception.PaymentProcessingException;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderConsumer {
    
    private final PaymentService paymentService;
    private final Logger log = LoggerFactory.getLogger(OrderConsumer.class);

    @RabbitListener(queues = "order.queue")
    public void consumeOrder(OrderDTO order) throws PaymentProcessingException {

        try {
            log.info("Received order: {}", order);
            paymentService.executePaymentProcessing(order);
            log.info("Successfully processed order: {}", order);
        } catch (MessageSendFailedException e) {
            log.error("Error processing order: {}", e.getMessage(), e);
            throw e;
        }
        
    }
}