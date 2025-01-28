package com.ms.stockservice.domain;


import com.ms.stockservice.model.dto.ProductDTO;
import com.ms.stockservice.model.entity.Product;
import com.ms.stockservice.model.entity.Stock;
import com.ms.stockservice.model.exception.ProductAlreadyExistsException;
import com.ms.stockservice.model.exception.ProductNotFoundException;
import com.ms.stockservice.repository.ProductRepository;
import com.ms.stockservice.repository.StockRepository;
import com.ms.stockservice.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service Implementation Tests")
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Captor
    private ArgumentCaptor<Product> productCaptor;

    @Captor
    private ArgumentCaptor<Stock> stockCaptor;

    private ProductDTO createValidProductDTO() {

        return ProductDTO.builder()
                .name("Test Product")
                .sku("TEST-SKU-001")
                .description("Test Description")
                .price(BigDecimal.valueOf(99.99))
                .stockQuantity(10)
                .build();

    }

    private Product createValidProduct() {

        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setSku("TEST-SKU-001");
        product.setDescription("Test Description");
        product.setPrice(BigDecimal.valueOf(99.99));
        return product;

    }

    @Nested
    @DisplayName("Create Product Tests")
    class CreateProductTests {

        @Test
        @DisplayName("Should successfully create product with stock")
        void shouldCreateProductWithStock() {

            ProductDTO inputDto = createValidProductDTO();
            Product product = createValidProduct();
            when(productRepository.findBySku(anyString())).thenReturn(Optional.empty());
            when(productRepository.save(any(Product.class))).thenReturn(product);


            ProductDTO result = productService.createProduct(inputDto);


            verify(productRepository).save(productCaptor.capture());
            verify(stockRepository).save(stockCaptor.capture());

            Product capturedProduct = productCaptor.getValue();
            Stock capturedStock = stockCaptor.getValue();

            assertThat(capturedProduct.getName()).isEqualTo(inputDto.getName());
            assertThat(capturedProduct.getSku()).isEqualTo(inputDto.getSku());
            assertThat(capturedStock.getQuantity()).isEqualTo(inputDto.getStockQuantity());
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should throw exception when SKU already exists")
        void shouldThrowExceptionWhenSkuExists() {

            ProductDTO inputDto = createValidProductDTO();
            when(productRepository.findBySku(anyString())).thenReturn(Optional.of(new Product()));

            assertThatThrownBy(() -> productService.createProduct(inputDto))
                    .isInstanceOf(ProductAlreadyExistsException.class)
                    .hasMessageContaining("already exists");


            verify(productRepository, never()).save(any());
            verify(stockRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Get Product Tests")
    class GetProductTests {

        @ParameterizedTest
        @MethodSource("com.ms.stockservice.domain.ProductServiceImplTest#provideInvalidIds")
        @DisplayName("Should throw exception when product not found by ID")
        void shouldThrowExceptionWhenProductNotFoundById(Long invalidId) {

            when(productRepository.findById(invalidId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.getProduct(invalidId))
                    .isInstanceOf(ProductNotFoundException.class)
                    .hasMessageContaining("not found");
        }

        @Test
        @DisplayName("Should successfully retrieve product by ID")
        void shouldRetrieveProductById() {

            Product product = createValidProduct();
            when(productRepository.findById(1L)).thenReturn(Optional.of(product));

            ProductDTO result = productService.getProduct(1L);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(product.getId());
            assertThat(result.getSku()).isEqualTo(product.getSku());
        }
    }

    @Nested
    @DisplayName("Update Product Tests")
    class UpdateProductTests {

        @Test
        @DisplayName("Should successfully update product")
        void shouldUpdateProduct() {

            Long productId = 1L;
            ProductDTO updateDto = createValidProductDTO();
            Product existingProduct = createValidProduct();

            when(productRepository.findById(productId)).thenReturn(Optional.of(existingProduct));
            when(productRepository.save(any(Product.class))).thenReturn(existingProduct);

            ProductDTO result = productService.updateProduct(productId, updateDto);

            verify(productRepository).save(productCaptor.capture());
            Product capturedProduct = productCaptor.getValue();

            assertThat(capturedProduct.getName()).isEqualTo(updateDto.getName());
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("Should throw exception when updating non-existent product")
        void shouldThrowExceptionWhenUpdatingNonExistentProduct() {

            Long productId = 999L;
            ProductDTO updateDto = createValidProductDTO();

            when(productRepository.findById(productId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.updateProduct(productId, updateDto))
                    .isInstanceOf(ProductNotFoundException.class);

            verify(productRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("Delete Product Tests")
    class DeleteProductTests {

        @Test
        @DisplayName("Should successfully delete product")
        void shouldDeleteProduct() {

            Long productId = 1L;
            Product product = createValidProduct();
            when(productRepository.findById(productId)).thenReturn(Optional.of(product));

            productService.deleteProduct(productId);

            verify(productRepository).delete(product);
        }

        @Test
        @DisplayName("Should throw exception when deleting non-existent product")
        void shouldThrowExceptionWhenDeletingNonExistentProduct() {

            Long productId = 999L;
            when(productRepository.findById(productId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> productService.deleteProduct(productId))
                    .isInstanceOf(ProductNotFoundException.class);

            verify(productRepository, never()).delete(any());
        }
    }

    // Método que fornece argumentos para testes parametrizados
    static Stream<Arguments> provideInvalidIds() {
        return Stream.of(
                Arguments.of(999L),
                Arguments.of(0L),
                Arguments.of(-1L)
        );
    }
}