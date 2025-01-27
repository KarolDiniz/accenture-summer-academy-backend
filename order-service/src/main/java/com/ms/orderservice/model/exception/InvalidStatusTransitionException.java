package com.ms.orderservice.model.exception;

import com.ms.orderservice.model.OrderStatus;

public class InvalidStatusTransitionException extends RuntimeException {
    
    public InvalidStatusTransitionException(OrderStatus previousStatus, OrderStatus requestedStatus) {
        super(String.format("Cannot transition from %s to %s", previousStatus, requestedStatus));
    }
}

