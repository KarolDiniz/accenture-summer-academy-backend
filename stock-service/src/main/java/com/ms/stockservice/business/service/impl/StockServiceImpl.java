package com.ms.stockservice.business.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ms.stockservice.business.service.StockService;
import com.ms.stockservice.business.service.producer.StockEventPublisher;
import com.ms.stockservice.domain.model.dto.StockCheckDTO;
import com.ms.stockservice.domain.model.dto.StockOperationDTO;
import com.ms.stockservice.domain.model.entity.Stock;
import com.ms.stockservice.domain.model.entity.StockOperation;
import com.ms.stockservice.domain.repository.StockOperationRepository;
import com.ms.stockservice.domain.repository.StockRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    private final StockRepository stockRepository;
    private final StockOperationRepository operationRepository;
    private final StockEventPublisher eventPublisher;
    private static final Logger log = LoggerFactory.getLogger(StockServiceImpl.class);

    @Override
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

    @Override
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
        eventPublisher.publishStockOperation(operationDTO);
    }

    @Override
    public Stock addStock(Stock stock) {
        return stockRepository.save(stock);
    }

    @Override
    @Transactional(readOnly = true)
    public Stock getStock(String sku) {
        return stockRepository.findByProductSku(sku)
                .orElseThrow(() -> new RuntimeException("Stock not found"));
    }
}
