package com.ms.orderservice.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.ms.orderservice.model.dto.OrderDTO;
import com.ms.orderservice.model.dto.PaymentDTO;
import com.ms.orderservice.model.entity.Order;
import com.ms.orderservice.model.entity.OrderItem;
import com.ms.orderservice.model.exception.MessageSendFailedException;
import com.ms.orderservice.service.OrderMapper;
import com.ms.orderservice.service.OrderService;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class PaymentConsumer {

    private final OrderService orderService;
    private final Logger log = LoggerFactory.getLogger(PaymentConsumer.class);
    private final OrderMapper orderMapper;

    @RabbitListener(queues = "payment.queue")
    @Transactional
    public void consumePayment(PaymentDTO payment) {
        try {
            log.info("Received payment result: {}", payment);

            if (payment == null || payment.getOrderId() == null) {
                log.error("Invalid payment data received");
                return;
            }

            String newStatus = "APPROVED".equals(payment.getStatus()) ? "CONFIRMED" : "CANCELLED";
            log.info("Updating order {} to status {}", payment.getOrderId(), newStatus);

            // O método getOrder agora será executado dentro de uma transação
            OrderDTO orderDto = orderService.getOrder(payment.getOrderId());
            Order order = orderMapper.toEntity(orderDto);

            // Se o pagamento foi aprovado, precisamos reservar o estoque para cada item
            if ("APPROVED".equals(payment.getStatus())) {
                log.info("Payment approved. Processing stock operations for order {}", payment.getOrderId());

                try {
                    // Agora podemos acessar os items com segurança
                    if (order.getItems() != null) {
                        for (OrderItem item : order.getItems()) {
                            log.info("Processing stock reservation for SKU {} quantity {}",
                                    item.getSku(), item.getQuantity());
                        }
                    }
                } catch (Exception e) {
                    log.error("Error processing stock operations: {}", e.getMessage(), e);
                    newStatus = "CANCELLED";
                }
            }

            orderService.updateOrderStatus(payment.getOrderId(), newStatus);
            log.info("Successfully updated order {} to status {}", payment.getOrderId(), newStatus);

        } catch (MessageSendFailedException e) {
            log.error("Error processing payment result: {}", e.getMessage(), e);
            throw e;
        }
    }
}
