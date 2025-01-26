package com.ms.paymentservice.consumer;

import com.ms.paymentservice.model.dto.OrderDTO;
import com.ms.paymentservice.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class OrderConsumerTest {

    private OrderConsumer consumer;
    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        // Mock do PaymentService
        paymentService = mock(PaymentService.class);
        // Instância do consumidor com o serviço mockado
        consumer = new OrderConsumer(paymentService);
    }

    @Test
    void consumeOrder_withValidOrder_shouldCallProcessPayment() {
        // Cenário: Pedido válido
        OrderDTO order = new OrderDTO();
        order.setId(1L);
        order.setPaymentMethod("CREDIT_CARD");

        // Executa o método do consumidor
        consumer.consumeOrder(order);

        // Verifica se o serviço de pagamento foi chamado com o pedido correto
        verify(paymentService).processPayment(order);
    }

    @Test
    void consumeOrder_withException_shouldThrowAndLogError() {
        // Cenário: Pedido inválido que lança exceção
        OrderDTO order = new OrderDTO();
        order.setId(1L);
        order.setPaymentMethod("INVALID");

        // Simula uma exceção ao processar o pagamento
        doThrow(new IllegalArgumentException("Invalid payment method"))
                .when(paymentService).processPayment(order);

        // Verifica se a exceção é lançada
        assertThrows(IllegalArgumentException.class, () -> consumer.consumeOrder(order));

        // Verifica se o serviço de pagamento foi chamado mesmo com erro
        verify(paymentService).processPayment(order);
    }

    @Test
    void consumeOrder_withNullOrder_shouldNotCallProcessPayment() {
        // Cenário: Pedido nulo
        OrderDTO order = null;

        // Executa o método do consumidor
        assertThrows(NullPointerException.class, () -> consumer.consumeOrder(order));

        // Verifica que o serviço de pagamento não foi chamado
        verifyNoInteractions(paymentService);
    }
}
