package com.ms.stockservice.business.service.producer;


import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import com.ms.stockservice.domain.model.dto.StockOperationDTO;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class StockEventPublisher {

    private final RabbitTemplate rabbitTemplate;
    private static final Logger log = LoggerFactory.getLogger(StockEventPublisher.class);

    public void publishStockOperation(StockOperationDTO operationDTO) {
        rabbitTemplate.convertAndSend(
                "stock.exchange",
                "stock.routingkey",
                operationDTO
        );
        log.info("Published stock operation event: {}", operationDTO);
    }
}