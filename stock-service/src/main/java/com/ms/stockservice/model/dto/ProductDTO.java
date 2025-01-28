package com.ms.stockservice.model.dto;


import com.ms.stockservice.model.entity.Product;
import com.ms.stockservice.model.entity.Stock;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductDTO {

    private Long id;

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "SKU is required")
    private String sku;

    private String description;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private BigDecimal price;

    private Integer stockQuantity;

    public static ProductDTO fromEntity(Product product) {
        return ProductDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStock() != null ? product.getStock().getQuantity() : 0)
                .build();
    }

    public Product toEntity() {
        Product product = new Product();
        product.setId(this.id);
        product.setName(this.name);
        product.setSku(this.sku);
        product.setDescription(this.description);
        product.setPrice(this.price);

        if (this.stockQuantity != null) {
            Stock stock = new Stock();
            stock.setQuantity(this.stockQuantity);
            stock.setProduct(product);
            product.setStock(stock);
        }

        return product;
    }

}
