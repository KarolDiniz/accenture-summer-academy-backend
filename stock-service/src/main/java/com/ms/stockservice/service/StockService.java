package com.ms.stockservice.service;


import com.ms.stockservice.model.Stock;
import com.ms.stockservice.model.StockOperation;
import com.ms.stockservice.repository.StockOperationRepository;
import com.ms.stockservice.repository.StockRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;
    private final StockOperationRepository operationRepository;
    private final RabbitTemplate rabbitTemplate;

    @Transactional
    public void processStockOperation(Long productId, Integer quantity, Long orderId, String operationType) {
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Stock not found for product: " + productId));

        StockOperation operation = new StockOperation();
        operation.setProductId(productId);
        operation.setOrderId(orderId);
        operation.setQuantity(quantity);
        operation.setOperationType(operationType);
        operation.setOperationDate(LocalDateTime.now());

        switch (operationType) {
            case "RESERVE":
                if (stock.getQuantity() >= quantity) {
                    stock.setQuantity(stock.getQuantity() - quantity);
                    operation.setStatus("SUCCESS");
                } else {
                    operation.setStatus("FAILED");
                    throw new RuntimeException("Insufficient stock");
                }
                break;
            case "CANCEL":
                stock.setQuantity(stock.getQuantity() + quantity);
                operation.setStatus("SUCCESS");
                break;
            default:
                operation.setStatus("INVALID_OPERATION");
                throw new RuntimeException("Invalid operation type");
        }

        stockRepository.save(stock);
        operationRepository.save(operation);

        // Notificar resultado da operação
        rabbitTemplate.convertAndSend(
                "stock.exchange",
                "stock.routingkey",
                operation
        );
    }

    public Stock addStock(Stock stock) {
        return stockRepository.save(stock);
    }

    @Transactional(readOnly = true)
    public Stock getStock(Long productId) {
        return stockRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Stock not found"));
    }
}