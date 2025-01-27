package com.ms.stockservice.service;

import com.ms.stockservice.model.OrderDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("order-service")
public interface OrderServiceClient {

    @GetMapping("/api/orders/{id}")
    OrderDTO getOrder(@PathVariable Long id);
}
