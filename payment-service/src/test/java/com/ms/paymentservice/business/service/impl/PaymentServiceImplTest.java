package com.ms.paymentservice.business.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.ms.paymentservice.business.service.PaymentStrategy;
import com.ms.paymentservice.business.service.PaymentStrategyFactory;
import com.ms.paymentservice.domain.model.dto.OrderDTO;
import com.ms.paymentservice.domain.model.dto.PaymentDTO;
import com.ms.paymentservice.domain.model.dto.PaymentDTO.OrderItemDTO;
import com.ms.paymentservice.domain.model.entity.Payment;
import com.ms.paymentservice.domain.model.entity.PaymentMethod;
import com.ms.paymentservice.domain.model.exception.InvalidPaymentMethodException;
import com.ms.paymentservice.domain.model.exception.PaymentProcessingException;
import com.ms.paymentservice.domain.repository.PaymentRepository;
import com.ms.paymentservice.infrastructure.producer.PaymentEventPublisher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private PaymentStrategyFactory paymentStrategyFactory;

    @Mock
    private PaymentEventPublisher eventPublisher;

    @Mock
    private ModelMapper modelMapper;

    @Mock
    private PaymentStrategy paymentStrategy;

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldThrowExceptionWhenOrderIsNull() {
        PaymentProcessingException exception = assertThrows(
            PaymentProcessingException.class,
            () -> paymentService.executePaymentProcessing(null)
        );

        assertEquals("Invalid order data", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenPaymentMethodIsInvalid() {
        OrderDTO order = new OrderDTO();
        order.setPaymentMethod(null);

        PaymentProcessingException exception = assertThrows(
            PaymentProcessingException.class,
            () -> paymentService.executePaymentProcessing(order)
        );

        assertEquals("Invalid order data", exception.getMessage());
    }

    @Test
    void shouldPublishPaymentProcessedEvent() {
        OrderDTO order = new OrderDTO();
        order.setPaymentMethod(PaymentMethod.PIX);

        Payment payment = new Payment();
        payment.setStatus("APPROVED");

        when(paymentStrategyFactory.getStrategy(PaymentMethod.PIX)).thenReturn(paymentStrategy);
        when(paymentStrategy.processPayment(order)).thenReturn(payment);
        when(paymentRepository.save(payment)).thenReturn(payment);

        PaymentDTO paymentDTO = new PaymentDTO();
        when(modelMapper.map(payment, PaymentDTO.class)).thenReturn(paymentDTO);

        try {
            paymentService.executePaymentProcessing(order);
        } catch (PaymentProcessingException e) {
            fail("PaymentProcessingException was thrown");
        }

        ArgumentCaptor<PaymentDTO> captor = ArgumentCaptor.forClass(PaymentDTO.class);
        verify(eventPublisher).publishPaymentProcessed(captor.capture());

        assertNotNull(captor.getValue());
    }

    

}
