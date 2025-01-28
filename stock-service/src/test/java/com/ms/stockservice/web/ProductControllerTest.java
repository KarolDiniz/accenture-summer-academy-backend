package com.ms.stockservice.web;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.ms.stockservice.controller.ProductController;
import com.ms.stockservice.model.exception.ProductAlreadyExistsException;
import com.ms.stockservice.model.exception.ProductNotFoundException;
import com.ms.stockservice.model.dto.ProductDTO;
import com.ms.stockservice.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@DisplayName("Product Controller Tests")
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private ProductDTO createValidProductDTO() {

        return ProductDTO.builder()
                .id(1L)
                .name("Test Product")
                .sku("TEST-SKU-001")
                .description("Test Description")
                .price(BigDecimal.valueOf(99.99))
                .stockQuantity(10)
                .build();

    }

    @Nested
    @DisplayName("GET /api/products")
    class GetAllProductsTests {

        @Test
        @DisplayName("Should return all products successfully")
        void shouldReturnAllProducts() throws Exception {

            List<ProductDTO> products = Arrays.asList(
                    createValidProductDTO(),
                    ProductDTO.builder()
                            .id(2L)
                            .name("Another Product")
                            .sku("TEST-SKU-002")
                            .price(BigDecimal.valueOf(149.99))
                            .stockQuantity(5)
                            .build()
            );

            when(productService.getAllProducts()).thenReturn(products);

            mockMvc.perform(get("/api/products"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$", hasSize(2)))
                    .andExpect(jsonPath("$[0].name", is("Test Product")))
                    .andExpect(jsonPath("$[1].name", is("Another Product")));
        }
    }

    @Nested
    @DisplayName("GET /api/products/{id}")
    class GetProductTests {

        @Test
        @DisplayName("Should return product when it exists")
        void shouldReturnProduct() throws Exception {

            ProductDTO product = createValidProductDTO();
            when(productService.getProduct(1L)).thenReturn(product);

            mockMvc.perform(get("/api/products/1"))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.id", is(1)))
                    .andExpect(jsonPath("$.name", is("Test Product")));
        }

        @Test
        @DisplayName("Should return 404 when product doesn't exist")
        void shouldReturn404WhenProductNotFound() throws Exception {

            when(productService.getProduct(999L))
                    .thenThrow(new ProductNotFoundException("Product not found with ID: 999"));

            mockMvc.perform(get("/api/products/999"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.message", containsString("not found")));
        }
    }

    @Nested
    @DisplayName("POST /api/products")
    class CreateProductTests {

        @Test
        @DisplayName("Should create product successfully")
        void shouldCreateProduct() throws Exception {

            ProductDTO inputDto = createValidProductDTO();
            when(productService.createProduct(any(ProductDTO.class))).thenReturn(inputDto);


            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name", is(inputDto.getName())))
                    .andExpect(jsonPath("$.sku", is(inputDto.getSku())));
        }

        @ParameterizedTest
        @NullAndEmptySource
        @ValueSource(strings = {"   "})
        @DisplayName("Should return 400 when name is invalid")
        void shouldReturn400WhenNameIsInvalid(String invalidName) throws Exception {

            ProductDTO inputDto = createValidProductDTO();
            inputDto.setName(invalidName);


            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputDto)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.errors.name", notNullValue()));
        }

        @Test
        @DisplayName("Should return 409 when SKU already exists")
        void shouldReturn409WhenSkuExists() throws Exception {

            ProductDTO inputDto = createValidProductDTO();
            when(productService.createProduct(any(ProductDTO.class)))
                    .thenThrow(new ProductAlreadyExistsException("Product with SKU already exists"));


            mockMvc.perform(post("/api/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputDto)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.message", containsString("already exists")));
        }
    }

    @Nested
    @DisplayName("PUT /api/products/{id}")
    class UpdateProductTests {

        @Test
        @DisplayName("Should update product successfully")
        void shouldUpdateProduct() throws Exception {

            ProductDTO inputDto = createValidProductDTO();
            when(productService.updateProduct(eq(1L), any(ProductDTO.class))).thenReturn(inputDto);


            mockMvc.perform(put("/api/products/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(inputDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name", is(inputDto.getName())));
        }
    }

    @Nested
    @DisplayName("DELETE /api/products/{id}")
    class DeleteProductTests {

        @Test
        @DisplayName("Should delete product successfully")
        void shouldDeleteProduct() throws Exception {

            doNothing().when(productService).deleteProduct(1L);


            mockMvc.perform(delete("/api/products/1"))
                    .andExpect(status().isNoContent());

            verify(productService).deleteProduct(1L);
        }

        @Test
        @DisplayName("Should return 404 when trying to delete non-existent product")
        void shouldReturn404WhenDeletingNonExistentProduct() throws Exception {

            doThrow(new ProductNotFoundException("Product not found"))
                    .when(productService).deleteProduct(999L);


            mockMvc.perform(delete("/api/products/999"))
                    .andExpect(status().isNotFound());
        }
    }
}