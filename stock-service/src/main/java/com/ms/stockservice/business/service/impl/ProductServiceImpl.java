package com.ms.stockservice.business.service.impl;

import com.ms.stockservice.business.service.ProductService;
import com.ms.stockservice.domain.model.dto.ProductDTO;
import com.ms.stockservice.domain.model.entity.Product;
import com.ms.stockservice.domain.model.entity.Stock;
import com.ms.stockservice.domain.model.exception.ProductAlreadyExistsException;
import com.ms.stockservice.domain.model.exception.ProductNotFoundException;
import com.ms.stockservice.domain.repository.ProductRepository;
import com.ms.stockservice.domain.repository.StockRepository;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private static final Logger log = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Override
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {

        log.info("Creating new product with SKU: {}", productDTO.getSku());

        validateUniqueSkuForCreate(productDTO.getSku());

        Product product = saveProductWithStock(productDTO.toEntity(), productDTO.getStockQuantity());

        log.info("Product created successfully with ID: {}", product.getId());

        return ProductDTO.fromEntity(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProduct(Long id) {

        log.info("Fetching product with ID: {}", id);

        return ProductDTO.fromEntity(findProductById(id));

    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductBySku(String sku) {

        log.info("Fetching product with SKU: {}", sku);

        return ProductDTO.fromEntity(findProductBySku(sku));

    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {

        log.info("Fetching all products");

        return productRepository.findAll().stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());

    }

    @Override
    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {

        log.info("Updating product with ID: {}", id);

        Product existingProduct = findProductById(id);
        validateUniqueSkuForUpdate(productDTO.getSku(), existingProduct.getSku());

        updateProductData(existingProduct, productDTO);
        Product updatedProduct = saveProductWithStock(existingProduct, productDTO.getStockQuantity());

        log.info("Product updated successfully with ID: {}", id);
        return ProductDTO.fromEntity(updatedProduct);

    }

    @Override
    @Transactional
    public void deleteProduct(Long id) {

        log.info("Deleting product with ID: {}", id);

        Product product = findProductById(id);
        productRepository.delete(product);

        log.info("Product deleted successfully with ID: {}", id);

    }

    // Métodos privados de validação e utilitários
    private Product findProductById(Long id) {

        return productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found with ID: {}", id);
                    return new ProductNotFoundException("Product not found with ID: " + id);
                });

    }

    private Product findProductBySku(String sku) {

        return productRepository.findBySku(sku)
                .orElseThrow(() -> {
                    log.error("Product not found with SKU: {}", sku);
                    return new ProductNotFoundException("Product not found with SKU: " + sku);
                });

    }

    private void validateUniqueSkuForCreate(String sku) {

        productRepository.findBySku(sku).ifPresent(p -> {
            log.error("Product with SKU {} already exists", sku);
            throw new ProductAlreadyExistsException("Product with SKU " + sku + " already exists");
        });

    }

    private void validateUniqueSkuForUpdate(String newSku, String currentSku) {

        if (!newSku.equals(currentSku)) {
            validateUniqueSkuForCreate(newSku);
        }

    }

    private void updateProductData(Product product, ProductDTO productDTO) {
        product.setName(productDTO.getName());
        product.setSku(productDTO.getSku());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
    }

    private Product saveProductWithStock(Product product, Integer stockQuantity) {
        product = productRepository.save(product);

        if (stockQuantity != null && stockQuantity > 0) {
            Stock stock = product.getStock();
            if (stock == null) {
                stock = new Stock();
                stock.setProduct(product);
                product.setStock(stock);
            }
            stock.setQuantity(stockQuantity);
            stockRepository.save(stock);
        }

        return product;
    }
}