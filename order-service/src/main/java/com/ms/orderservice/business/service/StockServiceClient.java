package com.ms.orderservice.business.service;

import com.ms.orderservice.domain.StockCheckDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "stock-service")
public interface StockServiceClient {

    @PostMapping("/api/stock/check-availability")
    List<StockCheckDTO> checkAvailability(@RequestBody List<StockCheckDTO> stockChecks);

}
