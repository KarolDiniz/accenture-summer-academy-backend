package com.ms.orderservice.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ms.orderservice.model.PaymentDTO;
import com.ms.orderservice.service.OrderService;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final OrderService orderService;
    private final Logger log = LoggerFactory.getLogger(PaymentConsumer.class);
   
    @RabbitListener(queues = "payment.queue")
    public void consumePayment(PaymentDTO payment) {
        try {
            log.info("Received payment: {}", payment);
            orderService.updateOrderStatus(payment.getOrderId(), payment.getStatus());
            log.info("Successfully processed payment: {}", payment);
        } catch (Exception e) {
            log.error("Error processing payment: {}", e.getMessage(), e);
            throw e; // Para rejeitar a mensagem e recolocá-la na fila
        }
    }
    
}
