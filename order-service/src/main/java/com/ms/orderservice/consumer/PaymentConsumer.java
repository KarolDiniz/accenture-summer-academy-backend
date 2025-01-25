package com.ms.orderservice.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ms.orderservice.model.dto.PaymentDTO;
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
            log.info("Received payment result: {}", payment);

            if (payment == null || payment.getOrderId() == null) {
                log.error("Invalid payment data received");
                return;
            }

            String newStatus = "APPROVED".equals(payment.getStatus()) ? "CONFIRMED" : "CANCELLED";
            log.info("Updating order {} to status {}", payment.getOrderId(), newStatus);

            orderService.updateOrderStatus(payment.getOrderId(), newStatus);
            log.info("Successfully updated order {} to status {}", payment.getOrderId(), newStatus);

        } catch (Exception e) {
            log.error("Error processing payment result: {}", e.getMessage(), e);
            throw e;
        }
    }
    
}
