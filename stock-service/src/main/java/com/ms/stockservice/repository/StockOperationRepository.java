package com.ms.stockservice.repository;


import com.ms.stockservice.model.entity.StockOperation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockOperationRepository extends JpaRepository<StockOperation, Long> {
}