package com.ms.stockservice.controller;

import com.ms.stockservice.model.entity.Stock;
import com.ms.stockservice.model.dto.StockCheckDTO;
import com.ms.stockservice.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @GetMapping("/{sku}")
    @Operation(summary = "Get stock by SKU")
    public ResponseEntity<Stock> getStock(@PathVariable String sku) {
        return ResponseEntity.ok(stockService.getStock(sku));
    }

    @PostMapping("/check-availability")
    @Operation(summary = "Check stock availability for multiple products")
    public ResponseEntity<List<StockCheckDTO>> checkAvailability(@RequestBody List<StockCheckDTO> stockChecks) {
        return ResponseEntity.ok(stockService.checkAvailability(stockChecks));
    }

}