package com.ms.stockservice.business.service;

import java.util.List;

import com.ms.stockservice.domain.model.dto.StockCheckDTO;
import com.ms.stockservice.domain.model.entity.Stock;

public interface StockService {

    List<StockCheckDTO> checkAvailability(List<StockCheckDTO> stockChecks);

    void processStockOperation(String sku, Integer quantity, Long orderId, String operationType);

    Stock addStock(Stock stock);

    Stock getStock(String sku);
}
