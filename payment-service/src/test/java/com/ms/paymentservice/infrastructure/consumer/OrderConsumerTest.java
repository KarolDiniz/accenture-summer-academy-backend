package com.ms.paymentservice.infrastructure.consumer;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.ms.paymentservice.business.service.PaymentService;
import com.ms.paymentservice.domain.model.dto.OrderDTO;
import com.ms.paymentservice.domain.model.dto.PaymentDTO;
import com.ms.paymentservice.domain.model.exception.MessageSendFailedException;
import com.ms.paymentservice.domain.model.exception.PaymentProcessingException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderConsumerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private OrderConsumer orderConsumer;

    private OrderDTO order;
    private PaymentDTO paymentDTO;

    @BeforeEach
    void setUp() {
        order = new OrderDTO();
        order.setId(1L);
        order.setCustomerEmail("customer@example.com");

        paymentDTO = new PaymentDTO();
        paymentDTO.setOrderId(order.getId());
        paymentDTO.setStatus("APPROVED");
    }

    @Test
    void shouldConsumeOrderSuccessfully() throws PaymentProcessingException {
        // Simula um pagamento bem-sucedido
        when(paymentService.executePaymentProcessing(order)).thenReturn(paymentDTO);

        assertDoesNotThrow(() -> orderConsumer.consumeOrder(order));

        verify(paymentService, times(1)).executePaymentProcessing(order);
    }

    @Test
    void shouldThrowMessageSendFailedExceptionWhenProcessingFails() throws PaymentProcessingException {
        // Simula uma falha no processamento do pagamento
        when(paymentService.executePaymentProcessing(order))
                .thenThrow(new MessageSendFailedException("Error sending message", new RuntimeException()));

        MessageSendFailedException exception = assertThrows(
            MessageSendFailedException.class,
            () -> orderConsumer.consumeOrder(order)
        );

        assertEquals("Error sending message", exception.getMessage());
        verify(paymentService, times(1)).executePaymentProcessing(order);
    }
}
