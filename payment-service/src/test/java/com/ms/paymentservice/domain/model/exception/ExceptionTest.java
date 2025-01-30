package com.ms.paymentservice.domain.model.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExceptionTest {

    @Test
    void shouldCreateInvalidPaymentMethodExceptionWithMessage() {
        InvalidPaymentMethodException exception = new InvalidPaymentMethodException("Invalid payment method");

        assertEquals("Invalid payment method", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void shouldCreateInvalidPaymentMethodExceptionWithMessageAndCause() {
        Throwable cause = new RuntimeException("Root cause");
        InvalidPaymentMethodException exception = new InvalidPaymentMethodException("Invalid payment method", cause);

        assertEquals("Invalid payment method", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void shouldCreateMessageSendFailedExceptionWithMessageAndCause() {
        Throwable cause = new RuntimeException("Kafka failure");
        MessageSendFailedException exception = new MessageSendFailedException("Failed to send message", cause);

        assertEquals("Failed to send message", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    @Test
    void shouldCreatePaymentProcessingExceptionWithMessage() {
        PaymentProcessingException exception = new PaymentProcessingException("Error processing payment");

        assertEquals("Error processing payment", exception.getMessage());
        assertNull(exception.getCause());
    }

    @Test
    void shouldCreatePaymentProcessingExceptionWithMessageAndCause() {
        Throwable cause = new RuntimeException("Database timeout");
        PaymentProcessingException exception = new PaymentProcessingException("Error processing payment", cause);

        assertEquals("Error processing payment", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}
