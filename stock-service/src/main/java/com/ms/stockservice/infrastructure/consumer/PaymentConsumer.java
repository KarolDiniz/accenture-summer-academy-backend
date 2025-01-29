package com.ms.stockservice.infrastructure.consumer;

import com.ms.stockservice.business.service.StockService;
import com.ms.stockservice.domain.model.dto.PaymentDTO;
import com.ms.stockservice.domain.model.dto.StockOperationDTO;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class PaymentConsumer {
    private final StockService stockService;
    private final Logger log = LoggerFactory.getLogger(PaymentConsumer.class);

    @RabbitListener(queues = "payment.stock.queue")
    @Transactional
    public void consumePayment(PaymentDTO payment) {

        try {

            log.info("Received payment result for order {}: {}", payment.getOrderId(), payment);

            if (payment == null || payment.getOrderId() == null) {

                log.error("Invalid payment data received");
                return;

            }

            if (payment.getItems() == null || payment.getItems().isEmpty()) {

                log.error("Payment data received without items for order {}", payment.getOrderId());
                return;

            }

            if ("APPROVED".equals(payment.getStatus())) {
                log.info("Processing stock confirmation for approved payment. Order: {}", payment.getOrderId());

                for (PaymentDTO.OrderItemDTO item : payment.getItems()) {

                    try {

                        stockService.processStockOperation(
                                item.getSku(),
                                item.getQuantity(),
                                payment.getOrderId(),
                                StockOperationDTO.OPERATION_CONFIRM  // Usando CONFIRM ao invés de RESERVE
                        );

                        log.info("Successfully confirmed stock for SKU: {}, Quantity: {}, Order: {}",
                                item.getSku(),
                                item.getQuantity(),
                                payment.getOrderId());

                    } catch (Exception e) {

                        log.error("Failed to confirm stock for SKU: {}, Order: {}, Error: {}",
                                item.getSku(),
                                payment.getOrderId(),
                                e.getMessage());
                        throw e;

                    }
                }

                log.info("Successfully processed all stock confirmations for order: {}", payment.getOrderId());

            } else if ("REJECTED".equals(payment.getStatus())) {
                // Se o pagamento for rejeitado, cancela a reserva
                for (PaymentDTO.OrderItemDTO item : payment.getItems()) {
                    stockService.processStockOperation(
                            item.getSku(),
                            item.getQuantity(),
                            payment.getOrderId(),
                            StockOperationDTO.OPERATION_CANCEL
                    );
                }
            }

        } catch (Exception e) {
            log.error("Error processing payment result: {}", e.getMessage(), e);
            throw e;
        }
    }

    private void rollbackReservations(Long orderId, List<PaymentDTO.OrderItemDTO> items) {
        log.info("Starting rollback of stock reservations for order: {}", orderId);

        for (PaymentDTO.OrderItemDTO item : items) {
            try {
                stockService.processStockOperation(
                        item.getSku(),
                        item.getQuantity(),
                        orderId,
                        "CANCEL"
                );
                log.info("Successfully rolled back stock reservation for SKU: {}, Order: {}",
                        item.getSku(),
                        orderId);

            } catch (Exception e) {
                log.error("Failed to rollback stock reservation for SKU: {}, Order: {}. Error: {}",
                        item.getSku(),
                        orderId,
                        e.getMessage());
            }
        }
    }
}