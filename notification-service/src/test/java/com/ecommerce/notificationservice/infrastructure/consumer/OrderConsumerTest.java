package com.ecommerce.notificationservice.infrastructure.consumer;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ecommerce.notificationservice.business.service.EmailService;
import com.ecommerce.notificationservice.domain.model.OrderDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class OrderConsumerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private OrderConsumer orderConsumer;

    private OrderDTO testOrder;

    @BeforeEach
    void setUp() {
        testOrder = new OrderDTO();
        testOrder.setId(1L);
        testOrder.setCustomerEmail("test@example.com");
        testOrder.setTotalAmount(new BigDecimal("100.00"));
        testOrder.setCreatedAt(LocalDateTime.now());
        testOrder.setStatus("CONFIRMED");
    }

    @Test
    void shouldProcessConfirmedOrderAndSendEmail() {
        // Act
        orderConsumer.handleOrderConfirmation(testOrder);

        // Assert
        verify(emailService, times(1)).sendOrderConfirmationEmail(testOrder);
    }

    @Test
    void shouldNotSendEmailForNonConfirmedOrder() {
        // Arrange
        testOrder.setStatus("PENDING");

        // Act
        orderConsumer.handleOrderConfirmation(testOrder);

        // Assert
        verify(emailService, never()).sendOrderConfirmationEmail(any());
    }

    @Test
    void shouldPropagateExceptionWhenEmailServiceFails() {
        // Arrange
        doThrow(new RuntimeException("Email service failed"))
            .when(emailService).sendOrderConfirmationEmail(any());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            orderConsumer.handleOrderConfirmation(testOrder));
    }

    @Test
    void shouldHandleNullOrderStatus() {
        // Arrange
        testOrder.setStatus(null);

        // Act
        orderConsumer.handleOrderConfirmation(testOrder);

        // Assert
        verify(emailService, never()).sendOrderConfirmationEmail(any());
    }

    @Test
    void shouldHandleNullOrder() {
        // Act & Assert
        assertThrows(NullPointerException.class, () -> 
            orderConsumer.handleOrderConfirmation(null));
    }
}