package com.ms.orderservice.model.exception;

public class MessageSendFailedException extends RuntimeException {

    public MessageSendFailedException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
