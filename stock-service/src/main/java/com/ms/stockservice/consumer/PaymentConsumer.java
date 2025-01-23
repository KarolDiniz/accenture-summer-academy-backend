package com.ms.stockservice.consumer;

import com.ms.stockservice.model.PaymentDTO;
import com.ms.stockservice.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PaymentConsumer {
    private final StockService stockService;

    @RabbitListener(queues = "payment.queue")
    public void consumePayment(PaymentDTO payment) {
        if ("APPROVED".equals(payment.getStatus())) {
            stockService.processStockOperation(
                    1L,
                    1,
                    payment.getOrderId(),
                    "RESERVE"  // Alterado para RESERVE
            );
        } else {
            stockService.processStockOperation(
                    1L,
                    1,
                    payment.getOrderId(),
                    "CANCEL"
            );
        }
    }
}