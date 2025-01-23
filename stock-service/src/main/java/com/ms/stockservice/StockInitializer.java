package com.ms.stockservice;

import com.ms.stockservice.model.Stock;
import com.ms.stockservice.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockInitializer implements CommandLineRunner {
    private final StockService stockService;

    @Override
    public void run(String... args) {
        // Verifica se já existem produtos, se não existirem, cria
        try {
            stockService.getStock(1L);
        } catch (RuntimeException e) {
            Stock defaultStock = new Stock();
            defaultStock.setProductId(1L);
            defaultStock.setProductName("Produto Padrão");
            defaultStock.setQuantity(100);
            defaultStock.setSku("SKU-001");

            stockService.addStock(defaultStock);
        }
    }
}
