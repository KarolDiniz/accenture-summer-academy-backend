package com.ms.stockservice.presentation.web;

import com.ms.stockservice.domain.model.exception.InvalidOperationException;
import com.ms.stockservice.domain.model.exception.ProductAlreadyExistsException;
import com.ms.stockservice.domain.model.exception.ProductNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    public void testHandleProductNotFoundException() {
        // Configuração do mock da exceção
        ProductNotFoundException exception = mock(ProductNotFoundException.class);
        when(exception.getMessage()).thenReturn("Product not found");

        // Chamando o método do handler
        ResponseEntity<ApiError> response = globalExceptionHandler.handleResourceNotFoundException(exception);

        // Verificação do status e mensagem
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Product not found", response.getBody().message());
    }

    @Test
    public void testHandleProductAlreadyExistsException() {
        // Configuração do mock da exceção
        ProductAlreadyExistsException exception = mock(ProductAlreadyExistsException.class);
        when(exception.getMessage()).thenReturn("Product already exists");

        // Chamando o método do handler
        ResponseEntity<ApiError> response = globalExceptionHandler.handleDuplicateResourceException(exception);

        // Verificação do status e mensagem
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Product already exists", response.getBody().message());
    }

    @Test
    public void testHandleInvalidOperationException() {
        // Configuração do mock da exceção
        InvalidOperationException exception = mock(InvalidOperationException.class);
        when(exception.getMessage()).thenReturn("Invalid operation");

        // Chamando o método do handler
        ResponseEntity<ApiError> response = globalExceptionHandler.handleInvalidOperationException(exception);

        // Verificação do status e mensagem
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid operation", response.getBody().message());
    }

    @Test
    public void testHandleGenericException() {
        // Exceção genérica
        Exception exception = new Exception("Generic error");

        // Chamando o método do handler
        ResponseEntity<ApiError> response = globalExceptionHandler.handleGenericException(exception);

        // Verificação do status e mensagem
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("An unexpected error occurred", response.getBody().message());
    }
    
      @Test
        public void testHandleDataIntegrityViolationException() {
            // Configuração da exceção
            DataIntegrityViolationException exception = mock(DataIntegrityViolationException.class);

            // Chamando o método do handler
            ResponseEntity<ApiError> response = globalExceptionHandler.handleDataIntegrityViolationException(exception);

            // Verificando o status e a mensagem de erro
            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
            assertEquals("Database constraint violation. The operation could not be completed.", response.getBody().message());
        }

}
