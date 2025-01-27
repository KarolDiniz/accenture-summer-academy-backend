package com.ms.orderservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.ms.orderservice.config.RabbitMQConfig;
import com.ms.orderservice.model.entity.Order;

@Service
public class RabbitMQService {

    private final RabbitTemplate rabbitTemplate;
    private static final Logger log = LoggerFactory.getLogger(RabbitMQService.class);

    public RabbitMQService(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendOrderToQueue(Order order) {
        
        try {
            // Enviando a mensagem para a fila
            rabbitTemplate.convertAndSend(RabbitMQConfig.ORDER_EXCHANGE,
                    RabbitMQConfig.ORDER_ROUTING_KEY, order);

            // Log de sucesso no envio da mensagem
            log.info("Message sent to {} with routing key {}: {}", 
                     RabbitMQConfig.ORDER_EXCHANGE, 
                     RabbitMQConfig.ORDER_ROUTING_KEY, 
                     order);
        } catch (Exception ex) {
            // Log de erro caso o envio falhe
            log.error("Failed to send message to {} with routing key {}: {}",
                      RabbitMQConfig.ORDER_EXCHANGE, 
                      RabbitMQConfig.ORDER_ROUTING_KEY, 
                      order, ex);

            throw new RuntimeException("Failed to process order", ex);
        }
    }
}


