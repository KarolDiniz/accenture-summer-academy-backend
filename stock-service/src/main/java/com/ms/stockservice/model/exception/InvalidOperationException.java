package com.ms.stockservice.model.exception;

public class InvalidOperationException extends RuntimeException {

    public InvalidOperationException(String message) {
        super(message);
    }

}
