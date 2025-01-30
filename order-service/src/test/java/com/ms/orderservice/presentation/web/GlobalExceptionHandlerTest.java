package com.ms.orderservice.presentation.web;

import com.ms.orderservice.domain.exception.InsufficientStockException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new DummyController())
                .setControllerAdvice(globalExceptionHandler)
                .build();
    }

    @Test
    void handleRuntimeException() {
        // Arrange
        RuntimeException ex = new RuntimeException("Internal server error");

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleRuntimeException(ex);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Internal server error", body.get("message"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), body.get("status"));
        assertEquals("Internal Server Error", body.get("error"));
        assertNotNull(body.get("timestamp"));
    }

    @Test
    void handleInsufficientStockException() {
        // Arrange
        InsufficientStockException ex = new InsufficientStockException(Arrays.asList("SKU1", "SKU2"));

        // Act
        ResponseEntity<Object> response = globalExceptionHandler.handleInsufficientStock(ex);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals("Insufficient stock for products: SKU1, SKU2", body.get("message"));
        assertEquals(HttpStatus.BAD_REQUEST.value(), body.get("status"));
        assertEquals("Insufficient Stock", body.get("error"));
        assertEquals(Arrays.asList("SKU1", "SKU2"), body.get("unavailableSkus"));
        assertNotNull(body.get("timestamp"));
    }

    // Classe de controle fictícia para simular exceções
    private static class DummyController {
        @GetMapping("/dummy-endpoint")
        public void throwRuntimeException() {
            throw new RuntimeException("Internal server error");
        }

        @GetMapping("/dummy-endpoint-insufficient-stock")
        public void throwInsufficientStockException() {
            throw new InsufficientStockException(Arrays.asList("SKU1", "SKU2"));
        }
    }
}