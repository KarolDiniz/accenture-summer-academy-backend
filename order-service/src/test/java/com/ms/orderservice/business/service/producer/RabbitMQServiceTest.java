package com.ms.orderservice.business.service.producer;

import com.ms.orderservice.domain.entity.Order;
import com.ms.orderservice.domain.exception.MessageSendFailedException;
import com.ms.orderservice.domain.exception.OrderNotFoundException;
import com.ms.orderservice.infrastructure.config.RabbitMQConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RabbitMQServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private RabbitMQService rabbitMQService;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
    }

    @Test
    void sendOrderToQueue_Success() {
        // Arrange
        doNothing().when(rabbitTemplate).convertAndSend(
            eq(RabbitMQConfig.ORDER_EXCHANGE),
            eq(RabbitMQConfig.ORDER_ROUTING_KEY),
            any(Order.class)
        );

        // Act
        assertDoesNotThrow(() -> rabbitMQService.sendOrderToQueue(testOrder));

        // Assert
        verify(rabbitTemplate).convertAndSend(
            eq(RabbitMQConfig.ORDER_EXCHANGE),
            eq(RabbitMQConfig.ORDER_ROUTING_KEY),
            eq(testOrder)
        );
    }

    @Test
    void sendOrderToQueue_NullOrder() {
        // Act & Assert
        assertThrows(OrderNotFoundException.class,
            () -> rabbitMQService.sendOrderToQueue(null));
        
        verify(rabbitTemplate, never()).convertAndSend(any(String.class), any(Object.class), any(CorrelationData.class));
    }

    @Test
    void sendOrderToQueue_RabbitMQFailure() {
        // Arrange
        doThrow(new RuntimeException("Connection failed"))
            .when(rabbitTemplate)
            .convertAndSend(any(String.class), any(Object.class), any(CorrelationData.class));

        // Act & Assert
        assertThrows(MessageSendFailedException.class,
            () -> rabbitMQService.sendOrderToQueue(testOrder));
    }

    @Test
    void sendOrderConfirmedNotification_Success() {
        // Act
        assertDoesNotThrow(() -> rabbitMQService.sendOrderConfirmedNotification(testOrder));

        // Assert
        verify(rabbitTemplate).convertAndSend(
            eq(RabbitMQConfig.ORDER_EXCHANGE),
            eq("order.confirmed.notification"),
            eq(testOrder)
        );
    }

    @Test
    void sendOrderConfirmedNotification_Failure() {
        // Arrange
        doThrow(new RuntimeException("Failed to send notification"))
            .when(rabbitTemplate)
            .convertAndSend(any(String.class), any(Object.class), any(CorrelationData.class));

        // Act & Assert
        assertThrows(RuntimeException.class,
            () -> rabbitMQService.sendOrderConfirmedNotification(testOrder));
    }
}