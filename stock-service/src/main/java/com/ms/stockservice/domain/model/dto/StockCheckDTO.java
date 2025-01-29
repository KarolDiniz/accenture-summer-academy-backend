package com.ms.stockservice.domain.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockCheckDTO {

    private String sku;
    private Integer quantityRequired;
    private boolean available;

    public StockCheckDTO(String sku, Integer quantityRequired) {
        this.sku = sku;
        this.quantityRequired = quantityRequired;
    }

}
