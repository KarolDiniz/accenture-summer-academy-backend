package com.ms.stockservice.controller.contract;

import com.ms.stockservice.model.dto.ProductDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Product Controller", description = "Endpoints for managing products")
public interface ProductControllerContract {


    @Operation(summary = "Create a new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created successfully",
                    content = @Content(schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "409", description = "Product with given SKU already exists")
    })
    ResponseEntity<ProductDTO> createProduct(@RequestBody @Valid ProductDTO productDTO);



    @Operation(summary = "Get a product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found",
                    content = @Content(schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    ResponseEntity<ProductDTO> getProduct(
            @Parameter(description = "Product ID", required = true) Long id);



    @Operation(summary = "Get a product by SKU")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found",
                    content = @Content(schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    ResponseEntity<ProductDTO> getProductBySku(
            @Parameter(description = "Product SKU", required = true) String sku);



    @Operation(summary = "Get all products")
    @ApiResponse(responseCode = "200", description = "List of all products",
            content = @Content(schema = @Schema(implementation = ProductDTO.class)))
    ResponseEntity<List<ProductDTO>> getAllProducts();



    @Operation(summary = "Update a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated successfully",
                    content = @Content(schema = @Schema(implementation = ProductDTO.class))),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    ResponseEntity<ProductDTO> updateProduct(
            @Parameter(description = "Product ID", required = true) Long id,
            @Valid ProductDTO productDTO);



    @Operation(summary = "Delete a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    ResponseEntity<Void> deleteProduct(
            @Parameter(description = "Product ID", required = true) Long id);
}