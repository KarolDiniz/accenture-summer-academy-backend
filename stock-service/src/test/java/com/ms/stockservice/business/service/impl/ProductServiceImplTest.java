package com.ms.stockservice.business.service.impl;

import com.ms.stockservice.domain.model.dto.ProductDTO;
import com.ms.stockservice.domain.model.entity.Product;
import com.ms.stockservice.domain.model.entity.Stock;
import com.ms.stockservice.domain.model.exception.ProductAlreadyExistsException;
import com.ms.stockservice.domain.model.exception.ProductNotFoundException;
import com.ms.stockservice.domain.repository.ProductRepository;
import com.ms.stockservice.domain.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateProduct_Success() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setSku("SKU123");
        productDTO.setStockQuantity(10);

        when(productRepository.findBySku("SKU123")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductDTO result = productService.createProduct(productDTO);

        assertNotNull(result);
        assertEquals("SKU123", result.getSku());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testCreateProduct_ThrowsProductAlreadyExistsException() {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setSku("SKU123");

        when(productRepository.findBySku("SKU123")).thenReturn(Optional.of(new Product()));

        assertThrows(ProductAlreadyExistsException.class, () -> productService.createProduct(productDTO));
    }

    @Test
    void testGetProductById_Success() {
        Product product = new Product();
        product.setId(1L);
        product.setSku("SKU123");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductDTO result = productService.getProduct(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("SKU123", result.getSku());
    }

    @Test
    void testGetProductById_ThrowsProductNotFoundException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ProductNotFoundException.class, () -> productService.getProduct(1L));
    }

    @Test
    void testGetAllProducts_Success() {
        Product product1 = new Product();
        product1.setId(1L);
        Product product2 = new Product();
        product2.setId(2L);

        when(productRepository.findAll()).thenReturn(List.of(product1, product2));

        List<ProductDTO> result = productService.getAllProducts();

        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void testUpdateProduct_Success() {
        Product existingProduct = new Product();
        existingProduct.setId(1L);
        existingProduct.setSku("SKU123");

        ProductDTO productDTO = new ProductDTO();
        productDTO.setSku("SKU456");
        productDTO.setStockQuantity(20);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.findBySku("SKU456")).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductDTO result = productService.updateProduct(1L, productDTO);

        assertNotNull(result);
        assertEquals("SKU456", result.getSku());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testDeleteProduct_Success() {
        Product product = new Product();
        product.setId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).delete(product);
    }
}