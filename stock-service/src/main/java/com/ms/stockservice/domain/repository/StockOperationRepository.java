package com.ms.stockservice.domain.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.ms.stockservice.domain.model.entity.StockOperation;

public interface StockOperationRepository extends JpaRepository<StockOperation, Long> {
}