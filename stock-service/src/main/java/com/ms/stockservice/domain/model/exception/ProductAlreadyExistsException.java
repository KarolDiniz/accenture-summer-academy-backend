package com.ms.stockservice.domain.model.exception;

public class ProductAlreadyExistsException extends RuntimeException {

  public ProductAlreadyExistsException(String message) {
    super(message);

  }
}
