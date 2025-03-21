package com.ms.orderservice.domain;


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

}
