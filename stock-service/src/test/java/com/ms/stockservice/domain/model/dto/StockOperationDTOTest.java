package com.ms.stockservice.domain.model.dto;

import org.junit.jupiter.api.Test;

import com.ms.stockservice.domain.model.entity.Product;
import com.ms.stockservice.domain.model.entity.StockOperation;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;

class StockOperationDTOTest {

    @Test
    void testStockOperationDTOBuilder() {
        StockOperationDTO stockOperationDTO = StockOperationDTO.builder()
                .id(1L)
                .orderId(123L)
                .productSku("SKU123")
                .productName("Product A")
                .quantity(10)
                .operationType(StockOperationDTO.OPERATION_RESERVE)
                .operationDate(LocalDateTime.now())
                .status(StockOperationDTO.STATUS_SUCCESS)
                .build();

        assertNotNull(stockOperationDTO);
        assertEquals(1L, stockOperationDTO.getId());
        assertEquals(123L, stockOperationDTO.getOrderId());
        assertEquals("SKU123", stockOperationDTO.getProductSku());
        assertEquals("Product A", stockOperationDTO.getProductName());
        assertEquals(10, stockOperationDTO.getQuantity());
        assertEquals(StockOperationDTO.OPERATION_RESERVE, stockOperationDTO.getOperationType());
        assertEquals(StockOperationDTO.STATUS_SUCCESS, stockOperationDTO.getStatus());
    }

    @Test
    void testFromOperation() {
        // Criando uma instância simulada de StockOperation
        StockOperation operation = new StockOperation();
        operation.setId(1L);
        operation.setOrderId(123L);
        operation.setQuantity(10);
        operation.setOperationType(StockOperationDTO.OPERATION_RESERVE);
        operation.setOperationDate(LocalDateTime.now());
        operation.setStatus(StockOperationDTO.STATUS_SUCCESS);
        
        // Simulando o produto associado à operação
        Product product = new Product();  // Supondo que Product é uma classe existente
        operation.setProduct(product);

        // Convertendo StockOperation para StockOperationDTO
        StockOperationDTO stockOperationDTO = StockOperationDTO.fromOperation(operation);

        // Testando a conversão
        assertNotNull(stockOperationDTO);
        assertEquals(operation.getId(), stockOperationDTO.getId());
        assertEquals(operation.getOrderId(), stockOperationDTO.getOrderId());
        assertEquals(product.getSku(), stockOperationDTO.getProductSku());
        assertEquals(product.getName(), stockOperationDTO.getProductName());
        assertEquals(operation.getQuantity(), stockOperationDTO.getQuantity());
        assertEquals(operation.getOperationType(), stockOperationDTO.getOperationType());
        assertEquals(operation.getStatus(), stockOperationDTO.getStatus());
        assertEquals(operation.getOperationDate(), stockOperationDTO.getOperationDate());
    }
}
