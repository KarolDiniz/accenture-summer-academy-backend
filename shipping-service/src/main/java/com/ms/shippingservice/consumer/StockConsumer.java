package com.ms.shippingservice.consumer;

import com.ms.shippingservice.model.ShippingOrder;
import com.ms.shippingservice.model.StockOperationDTO;
import com.ms.shippingservice.service.ShippingService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockConsumer {
    private final ShippingService shippingService;

    @RabbitListener(queues = "stock.queue")
    public void consumeStockOperation(StockOperationDTO stockOperation) {
        if ("SUCCESS".equals(stockOperation.getStatus()) &&
                "CONFIRM".equals(stockOperation.getOperationType())) {

            // Criar ordem de envio quando o estoque for confirmado
            ShippingOrder shippingOrder = new ShippingOrder();
            shippingOrder.setOrderId(stockOperation.getOrderId());
            // Aqui precisaríamos ter acesso aos dados de endereço do pedido
            // Em um cenário real, poderíamos ter esses dados no evento ou fazer uma consulta
            shippingService.createShippingOrder(shippingOrder);
        }
    }
}