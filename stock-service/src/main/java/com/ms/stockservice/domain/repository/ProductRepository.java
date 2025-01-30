package com.ms.stockservice.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ms.stockservice.domain.model.entity.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);
    List<Product> findBySkuIn(List<String> skus);

}
