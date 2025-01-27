package com.ms.stockservice.service;

import com.ms.stockservice.model.Stock;
import com.ms.stockservice.model.StockCheckDTO;
import com.ms.stockservice.model.StockOperation;
import com.ms.stockservice.model.StockOperationDTO;
import com.ms.stockservice.repository.ProductRepository;
import com.ms.stockservice.repository.StockOperationRepository;
import com.ms.stockservice.repository.StockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final ProductRepository productRepository;
    private final StockOperationRepository operationRepository;
    private final RabbitTemplate rabbitTemplate;
    private static final Logger log = LoggerFactory.getLogger(StockService.class);

    @Transactional(readOnly = true)
    public List<StockCheckDTO> checkAvailability(List<StockCheckDTO> stockChecks) {

        log.info("Checking stock availability for: {}", stockChecks);

        return stockChecks.stream()
                .map(check -> {

                    try {
                        Stock stock = stockRepository.findByProductSku(check.getSku())
                                .orElseThrow(() -> new RuntimeException("Stock not found for SKU: " + check.getSku()));

                        log.info("Found stock for SKU {}: quantity={}", check.getSku(), stock.getQuantity());
                        boolean isAvailable = stock.getQuantity() >= check.getQuantityRequired();
                        check.setAvailable(isAvailable);

                        if (!isAvailable) {
                            log.warn("Insufficient stock for SKU {}. Required: {}, Available: {}",
                                    check.getSku(),
                                    check.getQuantityRequired(),
                                    stock.getQuantity());
                        }

                    } catch (Exception e) {
                        log.error("Error checking stock for SKU {}: {}", check.getSku(), e.getMessage());
                        check.setAvailable(false);
                    }
                    return check;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void processStockOperation(String sku, Integer quantity, Long orderId, String operationType) {
        Stock stock = stockRepository.findByProductSku(sku)
                .orElseThrow(() -> new RuntimeException("Stock not found for SKU: " + sku));

        StockOperation operation = new StockOperation();
        operation.setProduct(stock.getProduct());
        operation.setOrderId(orderId);
        operation.setQuantity(quantity);
        operation.setOperationType(operationType);
        operation.setOperationDate(LocalDateTime.now());

        switch (operationType) {
            case StockOperationDTO.OPERATION_RESERVE:
            case StockOperationDTO.OPERATION_CONFIRM:
                if (stock.getQuantity() >= quantity) {
                    stock.setQuantity(stock.getQuantity() - quantity);
                    operation.setStatus(StockOperationDTO.STATUS_SUCCESS);
                } else {
                    operation.setStatus(StockOperationDTO.STATUS_FAILED);
                    throw new RuntimeException("Insufficient stock");
                }
                break;

            case StockOperationDTO.OPERATION_CANCEL:
                stock.setQuantity(stock.getQuantity() + quantity);
                operation.setStatus(StockOperationDTO.STATUS_SUCCESS);
                break;

            default:
                operation.setStatus(StockOperationDTO.STATUS_FAILED);
                throw new RuntimeException("Invalid operation type");
        }

        stock = stockRepository.saveAndFlush(stock);
        operationRepository.save(operation);

        StockOperationDTO operationDTO = StockOperationDTO.fromOperation(operation);
        rabbitTemplate.convertAndSend(
                "stock.exchange",
                "stock.routingkey",
                operationDTO
        );
    }

    public Stock addStock(Stock stock) {
        return stockRepository.save(stock);
    }

    @Transactional(readOnly = true)
    public Stock getStock(String sku) {
        return stockRepository.findByProductSku(sku)
                .orElseThrow(() -> new RuntimeException("Stock not found"));
    }
}