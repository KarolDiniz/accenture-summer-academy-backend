package com.ms.stockservice.model.exception;

public class ProductAlreadyExistsException extends RuntimeException {

  public ProductAlreadyExistsException(String message) {
    super(message);

  }
}
