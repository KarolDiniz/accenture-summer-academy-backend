package com.ms.stockservice.domain.model.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class StockCheckDTOTest {

    @Test
    void testStockCheckDTOConstructor() {
        StockCheckDTO stockCheckDTO = new StockCheckDTO("SKU123", 10);
        assertNotNull(stockCheckDTO);
        assertEquals("SKU123", stockCheckDTO.getSku());
        assertEquals(10, stockCheckDTO.getQuantityRequired());
        assertFalse(stockCheckDTO.isAvailable());
    }

    @Test
    void testStockCheckDTOAvailability() {
        StockCheckDTO stockCheckDTO = new StockCheckDTO("SKU123", 5);
        stockCheckDTO.setAvailable(true);
        assertTrue(stockCheckDTO.isAvailable());
    }
}
