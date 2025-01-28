package com.ms.orderservice.model.exception;

import java.util.List;

public class InsufficientStockException extends RuntimeException {

    private final List<String> unavailableSkus;

    public InsufficientStockException(List<String> unavailableSkus) {
        super("Insufficient stock for products: " + String.join(", ", unavailableSkus));
        this.unavailableSkus = unavailableSkus;
    }

    public List<String> getUnavailableSkus() {
        return unavailableSkus;
    }
}
