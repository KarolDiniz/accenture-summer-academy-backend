package com.ms.stockservice.repository;

import com.ms.stockservice.model.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface StockRepository extends JpaRepository<Stock, Long> {
    @Lock(LockModeType.OPTIMISTIC)
    Optional<Stock> findByProductId(Long productId);
}