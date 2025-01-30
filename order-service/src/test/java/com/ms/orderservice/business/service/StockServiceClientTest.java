package com.ms.orderservice.business.service;

import com.ms.orderservice.domain.StockCheckDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StockServiceClientTest {

    @Mock
    private StockServiceClient stockServiceClient;

    @Test
    void checkAvailability_Success() {
        // Arrange
        StockCheckDTO stockCheck = new StockCheckDTO("TEST-SKU", 1, true);
        when(stockServiceClient.checkAvailability(any()))
            .thenReturn(Collections.singletonList(stockCheck));

        // Act
        List<StockCheckDTO> result = stockServiceClient.checkAvailability(Collections.singletonList(stockCheck));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("TEST-SKU", result.get(0).getSku());
        assertTrue(result.get(0).isAvailable());
    }

    @Test
    void checkAvailability_EmptyList() {
        // Arrange
        when(stockServiceClient.checkAvailability(any()))
            .thenReturn(Collections.emptyList());

        // Act
        List<StockCheckDTO> result = stockServiceClient.checkAvailability(Collections.emptyList());

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}