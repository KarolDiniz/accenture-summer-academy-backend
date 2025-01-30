package com.ms.stockservice.business.service.impl;

import com.ms.stockservice.business.service.producer.StockEventPublisher;
import com.ms.stockservice.domain.model.dto.StockCheckDTO;
import com.ms.stockservice.domain.model.dto.StockOperationDTO;
import com.ms.stockservice.domain.model.entity.Stock;
import com.ms.stockservice.domain.model.entity.StockOperation;
import com.ms.stockservice.domain.repository.StockOperationRepository;
import com.ms.stockservice.domain.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class StockServiceImplTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private StockOperationRepository operationRepository;

    @Mock
    private StockEventPublisher eventPublisher;

    @InjectMocks
    private StockServiceImpl stockService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCheckAvailability_Success() {
        StockCheckDTO checkDTO = new StockCheckDTO();
        checkDTO.setSku("SKU123");
        checkDTO.setQuantityRequired(5);

        Stock stock = new Stock();
        stock.setQuantity(10);

        when(stockRepository.findByProductSku("SKU123")).thenReturn(Optional.of(stock));

        List<StockCheckDTO> result = stockService.checkAvailability(List.of(checkDTO));

        assertNotNull(result);
        assertTrue(result.get(0).isAvailable());
    }

    @Test
    void testCheckAvailability_InsufficientStock() {
        StockCheckDTO checkDTO = new StockCheckDTO();
        checkDTO.setSku("SKU123");
        checkDTO.setQuantityRequired(15);

        Stock stock = new Stock();
        stock.setQuantity(10);

        when(stockRepository.findByProductSku("SKU123")).thenReturn(Optional.of(stock));

        List<StockCheckDTO> result = stockService.checkAvailability(List.of(checkDTO));

        assertNotNull(result);
        assertFalse(result.get(0).isAvailable());
    }

    @Test
    void testProcessStockOperation_InsufficientStock() {
        Stock stock = new Stock();
        stock.setQuantity(10);

        when(stockRepository.findByProductSku("SKU123")).thenReturn(Optional.of(stock));

        assertThrows(RuntimeException.class, () ->
                stockService.processStockOperation("SKU123", 15, 1L, StockOperationDTO.OPERATION_RESERVE));
    }

    @Test
    void testAddStock_Success() {
        Stock stock = new Stock();
        stock.setQuantity(10);

        when(stockRepository.save(any(Stock.class))).thenReturn(stock);

        Stock result = stockService.addStock(stock);

        assertNotNull(result);
        assertEquals(10, result.getQuantity());
    }

    @Test
    void testGetStock_Success() {
        Stock stock = new Stock();
        stock.setQuantity(10);

        when(stockRepository.findByProductSku("SKU123")).thenReturn(Optional.of(stock));

        Stock result = stockService.getStock("SKU123");

        assertNotNull(result);
        assertEquals(10, result.getQuantity());
    }

    @Test
    void testGetStock_ThrowsException() {
        when(stockRepository.findByProductSku("SKU123")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> stockService.getStock("SKU123"));
    }
}