package com.ms.orderservice.domain.exception;

import com.ms.orderservice.domain.entity.OrderStatus;

public class InvalidStatusTransitionException extends RuntimeException {

    public InvalidStatusTransitionException(OrderStatus previousStatus, OrderStatus requestedStatus) {
        super(String.format("Cannot transition from %s to %s", previousStatus, requestedStatus));
    }

}

