package com.ms.orderservice.domain.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MessageSendFailedExceptionTest {

    @Test
    void testMessageSendFailedException() {
        // Arrange
        String message = "Failed to send message";
        Throwable cause = new RuntimeException("Connection failed");

        // Act
        MessageSendFailedException exception = new MessageSendFailedException(message, cause);

        // Assert
        assertEquals(message, exception.getMessage());
        assertEquals(cause, exception.getCause());
    }
}