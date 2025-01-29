package com.ms.orderservice.business.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.ms.orderservice.infrastructure.config.RabbitMQConfig;
import com.ms.orderservice.domain.entity.Order;
import com.ms.orderservice.domain.exception.MessageSendFailedException;
import com.ms.orderservice.domain.exception.OrderNotFoundException;

import io.swagger.v3.oas.annotations.Operation;

@Service
public class RabbitMQService {

    private final RabbitTemplate rabbitTemplate;
    private static final Logger log = LoggerFactory.getLogger(RabbitMQService.class);

    public RabbitMQService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @Operation(
        summary = "Sends a request to the RabbitMQ queue"
    )
    public void sendOrderToQueue(Order order) {
        try {
            if (order == null || order.getId() == null) {
                throw new OrderNotFoundException("Order or order ID is invalid");
            }

            // Enviando a mensagem para a fila
            rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE,
                    RabbitMQConfig.ORDER_ROUTING_KEY, order);

            // Log de sucesso no envio da mensagem
            log.info("Message sent to {} with routing key {}: {}", 
                     RabbitMQConfig.ORDER_EXCHANGE, 
                     RabbitMQConfig.ORDER_ROUTING_KEY, 
                     order);
        } catch (OrderNotFoundException ex) {
            log.error("Invalid message format: {}", ex.getMessage());
            throw ex; // Re-lança a exceção, se necessário
        } catch (Exception ex) {
            log.error("Failed to send message to {} with routing key {}: {}",
                      RabbitMQConfig.ORDER_EXCHANGE, 
                      RabbitMQConfig.ORDER_ROUTING_KEY, 
                      order, ex);

            throw new MessageSendFailedException("Failed to send order message to RabbitMQ", ex);
        }
    }
    
    public void sendOrderConfirmedNotification(Order order) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDER_EXCHANGE,
                    "order.confirmed.notification",
                    order
            );
            log.info("Order confirmation notification sent for order ID: {}", order.getId());
        } catch (Exception ex) {
            log.error("Failed to send order confirmation notification: {}", ex.getMessage());
            throw new RuntimeException("Failed to send order confirmation notification to RabbitMQ", ex);
        }
}

}





