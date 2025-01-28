package com.ms.stockservice.service;

import com.ms.stockservice.model.dto.ProductDTO;
import java.util.List;

public interface ProductService {

    ProductDTO createProduct(ProductDTO productDTO);

    ProductDTO getProduct(Long id);

    ProductDTO getProductBySku(String sku);

    List<ProductDTO> getAllProducts();

    ProductDTO updateProduct(Long id, ProductDTO productDTO);

    void deleteProduct(Long id);
}