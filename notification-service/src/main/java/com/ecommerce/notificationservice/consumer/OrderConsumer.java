package com.ecommerce.notificationservice.consumer;


import com.ecommerce.notificationservice.model.OrderDTO;
import com.ecommerce.notificationservice.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderConsumer {

    private final EmailService emailService;
    private static final Logger log = LoggerFactory.getLogger(OrderConsumer.class);

    @RabbitListener(queues = "order.confirmed.queue")
    public void handleOrderConfirmation(OrderDTO order) {
        log.info("Received confirmed order notification: {}", order);

        try {

            if ("CONFIRMED".equals(order.getStatus())) {

                emailService.sendOrderConfirmationEmail(order);
                log.info("Confirmation email processed succesfully for order: {}", order);

            } else {
                log.info("Order {} is not confirmed. Status: {}. Skipping email.",
                        order.getId(),
                        order.getStatus());
            }
        } catch (Exception ex) {

            log.error("Error processing order confirmation: {}",
                    ex.getMessage(),
                    ex);

            throw ex;
        }
    }

}
