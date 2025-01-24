package com.ms.orderservice.model.exception;

public class InvalidStatusTransitionException extends RuntimeException {

  public InvalidStatusTransitionException(String currentStatus, String newStatus) {
    super(String.format("Cannot transition from status %s to %s", currentStatus, newStatus));
  }

}
