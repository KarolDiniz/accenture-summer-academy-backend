package com.ms.paymentservice.domain.model.exception;

public class MessageSendFailedException extends RuntimeException {

    public MessageSendFailedException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
