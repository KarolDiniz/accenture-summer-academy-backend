package com.ms.stockservice.business.service;

import java.util.List;

import com.ms.stockservice.domain.model.dto.ProductDTO;

public interface ProductService {

    ProductDTO createProduct(ProductDTO productDTO);

    ProductDTO getProduct(Long id);

    ProductDTO getProductBySku(String sku);

    List<ProductDTO> getAllProducts();

    ProductDTO updateProduct(Long id, ProductDTO productDTO);

    void deleteProduct(Long id);
}