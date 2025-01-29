package com.ms.stockservice.domain.model.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String message) {
        super(message);

    }
}
