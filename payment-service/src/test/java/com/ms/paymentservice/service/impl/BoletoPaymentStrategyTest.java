package com.ms.paymentservice.service.impl;

import com.ms.paymentservice.model.Payment;
import com.ms.paymentservice.model.dto.OrderDTO;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class BoletoPaymentStrategyTest {

    private final BoletoPaymentStrategy strategy = new BoletoPaymentStrategy();

    @Test
    void processPayment_shouldReturnPendingPayment() {
        // Criar um objeto OrderDTO para teste
        OrderDTO order = new OrderDTO();
        order.setId(1L);
        order.setCustomerEmail("customer@test.com");
        order.setTotalAmount(BigDecimal.valueOf(100.0));

        // Processar o pagamento com a estratégia de boleto
        Payment payment = strategy.processPayment(order);

        // Validar os resultados
        assertNotNull(payment);
        assertEquals("PENDING", payment.getStatus());
        assertEquals(order.getId(), payment.getOrderId());
        assertNotNull(payment.getPaymentLink());
        assertTrue(payment.getPaymentLink().contains("orderId=1"));
    }
    @Test
    void processPayment_shouldCreatePendingPayment() {
        OrderDTO order = new OrderDTO();
        order.setId(1L);
        order.setCustomerEmail("customer@example.com");
        order.setTotalAmount(BigDecimal.valueOf(200.00));

        Payment payment = strategy.processPayment(order);

        assertNotNull(payment);
        assertEquals("PENDING", payment.getStatus());
        assertEquals("BOLETO", payment.getPaymentMethod());
        assertEquals("https://paymentservice.com/gerar-boleto?orderId=1", payment.getPaymentLink());
    }
}
