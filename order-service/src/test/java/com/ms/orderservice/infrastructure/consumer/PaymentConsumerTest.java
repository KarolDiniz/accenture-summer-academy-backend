package com.ms.orderservice.infrastructure.consumer;

import static org.mockito.Mockito.*;

import com.ms.orderservice.business.service.OrderMapper;
import com.ms.orderservice.business.service.OrderService;
import com.ms.orderservice.domain.dto.OrderDTO;
import com.ms.orderservice.domain.dto.PaymentDTO;
import com.ms.orderservice.domain.entity.Order;
import com.ms.orderservice.domain.entity.OrderItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class PaymentConsumerTest {

    @Mock
    private OrderService orderService;
    
    @Mock
    private OrderMapper orderMapper;
    
    @Mock
    private Logger log;

    @InjectMocks
    private PaymentConsumer paymentConsumer;

    private PaymentDTO paymentDTO;
    private OrderDTO orderDTO;
    private Order order;

    @BeforeEach
    void setUp() {
        paymentDTO = new PaymentDTO();
        paymentDTO.setOrderId(1L);
        paymentDTO.setStatus("APPROVED");

        orderDTO = new OrderDTO();
        orderDTO.setId(1L);
        orderDTO.setItems(Collections.emptyList());

        order = new Order();
        order.setId(1L);
        order.setItems(Collections.emptyList());
    }

    @Test
    void consumePayment_SuccessApproved() {
        when(orderService.getOrder(1L)).thenReturn(orderDTO);
        when(orderMapper.toEntity(orderDTO)).thenReturn(order);

        paymentConsumer.consumePayment(paymentDTO);

        verify(orderService, times(1)).updateOrderStatus(1L, "CONFIRMED");
    }

    @Test
    void consumePayment_SuccessCancelled() {
        paymentDTO.setStatus("REJECTED");
        
        when(orderService.getOrder(1L)).thenReturn(orderDTO);
        when(orderMapper.toEntity(orderDTO)).thenReturn(order);

        paymentConsumer.consumePayment(paymentDTO);

        verify(orderService, times(1)).updateOrderStatus(1L, "CANCELLED");
    }

    @Test
    void consumePayment_InvalidPayment() {
        paymentConsumer.consumePayment(null);
        verify(orderService, never()).updateOrderStatus(anyLong(), anyString());
    }
}