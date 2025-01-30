package com.ms.stockservice.infrastructure;

import com.ms.stockservice.business.service.StockService;
import com.ms.stockservice.domain.model.dto.PaymentDTO;
import com.ms.stockservice.domain.model.dto.StockOperationDTO;
import com.ms.stockservice.infrastructure.consumer.PaymentConsumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import java.util.Collections;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentConsumerTest {

    @Mock
    private StockService stockService;

    @Mock
    private Logger log;

    @InjectMocks
    private PaymentConsumer paymentConsumer;

    private PaymentDTO payment;

    @BeforeEach
    void setUp() {
        payment = new PaymentDTO();
        payment.setOrderId(1L);
        payment.setStatus("APPROVED");
        
        PaymentDTO.OrderItemDTO item = new PaymentDTO.OrderItemDTO();
        item.setSku("SKU123");
        item.setQuantity(2);
        payment.setItems(List.of(item));
    }

    @Test
    void shouldProcessStockConfirmationWhenPaymentIsApproved() {
        paymentConsumer.consumePayment(payment);

        verify(stockService, times(1)).processStockOperation(
                "SKU123", 2, 1L, StockOperationDTO.OPERATION_CONFIRM);
    }

    @Test
    void shouldRollbackReservationsWhenPaymentIsRejected() {
        payment.setStatus("REJECTED");
        paymentConsumer.consumePayment(payment);

        verify(stockService, times(1)).processStockOperation(
                "SKU123", 2, 1L, StockOperationDTO.OPERATION_CANCEL);
    }

    @Test
    void shouldNotProcessStockWhenOrderIdIsNull() {
        payment.setOrderId(null);
        paymentConsumer.consumePayment(payment);
        verify(stockService, never()).processStockOperation(any(), any(), any(), any());
    }

    @Test
    void shouldNotProcessStockWhenItemsAreEmpty() {
        payment.setItems(Collections.emptyList());
        paymentConsumer.consumePayment(payment);
        verify(stockService, never()).processStockOperation(any(), any(), any(), any());
    }
}
