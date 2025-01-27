package com.ms.paymentservice.model.exception;

public class MessageSendFailedException extends RuntimeException {

    public MessageSendFailedException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
