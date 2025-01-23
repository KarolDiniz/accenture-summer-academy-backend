package com.ms.stockservice.controller;

import com.ms.stockservice.model.Stock;
import com.ms.stockservice.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/stock")
@RequiredArgsConstructor
@Tag(name = "Stock Controller", description = "Endpoints for managing stock")
public class StockController {
    private final StockService stockService;

    @PostMapping
    @Operation(summary = "Add new stock")
    public ResponseEntity<Stock> addStock(@RequestBody Stock stock) {
        return ResponseEntity.ok(stockService.addStock(stock));
    }

    @GetMapping("/{productId}")
    @Operation(summary = "Get stock by product ID")
    public ResponseEntity<Stock> getStock(@PathVariable Long productId) {
        return ResponseEntity.ok(stockService.getStock(productId));
    }
}