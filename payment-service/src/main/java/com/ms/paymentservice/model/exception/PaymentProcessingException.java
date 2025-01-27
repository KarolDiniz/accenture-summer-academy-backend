package com.ms.paymentservice.model.exception;

public class PaymentProcessingException extends Exception {
    
    public PaymentProcessingException(String message) {
        super(message);
    }

    public PaymentProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}
