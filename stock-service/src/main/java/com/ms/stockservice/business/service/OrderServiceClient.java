package com.ms.stockservice.business.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.ms.stockservice.domain.model.dto.OrderDTO;

@FeignClient("order-service")
public interface OrderServiceClient {

    @GetMapping("/api/orders/{id}")
    OrderDTO getOrder(@PathVariable Long id);
}
