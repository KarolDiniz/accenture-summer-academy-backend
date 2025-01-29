package com.ms.orderservice.domain.exception;

public class MessageSendFailedException extends RuntimeException {

    public MessageSendFailedException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
