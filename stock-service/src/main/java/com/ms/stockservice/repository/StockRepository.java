package com.ms.stockservice.repository;

import com.ms.stockservice.model.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.util.List;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {

    @Lock(LockModeType.OPTIMISTIC)
    Optional<Stock> findByProductId(Long productId);

    @Lock(LockModeType.OPTIMISTIC)
    Optional<Stock> findByProductSku(String sku);

    List<Stock> findByProductSkuIn(List<String> skus);

}