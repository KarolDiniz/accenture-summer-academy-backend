package com.ms.paymentservice.service;

import com.ms.paymentservice.model.Payment;
import com.ms.paymentservice.model.PaymentMethod;
import com.ms.paymentservice.model.dto.OrderDTO;
import com.ms.paymentservice.model.dto.PaymentDTO;
import com.ms.paymentservice.repository.PaymentRepository;
import com.ms.paymentservice.service.impl.PaymentStrategyFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

    private PaymentService paymentService;
    private RabbitTemplate rabbitTemplate;
    private PaymentRepository paymentRepository;
    private PaymentStrategyFactory paymentStrategyFactory;
    private ModelMapper modelMapper;

    @BeforeEach
    void setUp() {
        rabbitTemplate = mock(RabbitTemplate.class);
        paymentRepository = mock(PaymentRepository.class);
        paymentStrategyFactory = mock(PaymentStrategyFactory.class);
        modelMapper = new ModelMapper();

        paymentService = new PaymentService(rabbitTemplate, paymentRepository, paymentStrategyFactory, modelMapper);
    }

    @Test
    void shouldProcessCreditCardPaymentSuccessfully() {
        // Arrange
        OrderDTO order = new OrderDTO();
        order.setId(1L);
        order.setCustomerEmail("test@example.com");
        order.setTotalAmount(BigDecimal.valueOf(100.00));
        order.setPaymentMethod("CREDIT_CARD");

        PaymentStrategy mockStrategy = mock(PaymentStrategy.class);
        when(paymentStrategyFactory.getStrategy(PaymentMethod.CREDIT_CARD)).thenReturn(mockStrategy);

        Payment mockPayment = new Payment();
        mockPayment.setId(1L);
        mockPayment.setStatus("APPROVED");

        when(mockStrategy.processPayment(order)).thenReturn(mockPayment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(mockPayment);

        // Act
        PaymentDTO result = paymentService.processPayment(order);

        // Assert
        assertNotNull(result);
        assertEquals("APPROVED", result.getStatus());
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any(Payment.class));
    }

    @Test
    void processPayment_withValidOrder_shouldProcessSuccessfully() {
        // Mocking the OrderDTO
        OrderDTO order = new OrderDTO();
        order.setId(1L);
        order.setCustomerEmail("customer@test.com");
        order.setTotalAmount(BigDecimal.valueOf(100.0));
        order.setPaymentMethod("CREDIT_CARD");

        // Mocking the PaymentStrategy
        Payment payment = new Payment();
        payment.setOrderId(order.getId());
        payment.setAmount(order.getTotalAmount());
        payment.setPaymentDate(LocalDateTime.now());
        payment.setStatus("APPROVED");
        payment.setPaymentMethod("CREDIT_CARD");

        PaymentStrategy paymentStrategy = mock(PaymentStrategy.class);
        when(paymentStrategy.processPayment(order)).thenReturn(payment);
        when(paymentStrategyFactory.getStrategy(PaymentMethod.CREDIT_CARD)).thenReturn(paymentStrategy);
        when(paymentRepository.save(any(Payment.class))).thenReturn(payment);

        // Processing the payment
        PaymentDTO result = paymentService.processPayment(order);

        // Verifications
        assertNotNull(result);
        assertEquals(order.getId(), result.getOrderId());
        assertEquals("APPROVED", result.getStatus());
        verify(rabbitTemplate).convertAndSend(eq("payment.exchange"), eq("payment.routingkey"), any(Payment.class));
    }

    @Test
    void shouldThrowExceptionForInvalidAmount() {
        // Arrange
        OrderDTO order = new OrderDTO();
        order.setId(1L);
        order.setCustomerEmail("test@example.com");
        order.setTotalAmount(BigDecimal.ZERO);
        order.setPaymentMethod("CREDIT_CARD");

        // Act & Assert
        Exception exception = assertThrows(IllegalArgumentException.class, () -> paymentService.processPayment(order));
        assertEquals("Payment amount must be greater than zero", exception.getMessage());
        verifyNoInteractions(rabbitTemplate);
    }

    @Test
    void shouldHandleBoletoPaymentCorrectly() {
        // Arrange
        OrderDTO order = new OrderDTO();
        order.setId(1L);
        order.setCustomerEmail("test@example.com");
        order.setTotalAmount(BigDecimal.valueOf(200.00));
        order.setPaymentMethod("BOLETO");

        PaymentStrategy mockStrategy = mock(PaymentStrategy.class);
        when(paymentStrategyFactory.getStrategy(PaymentMethod.BOLETO)).thenReturn(mockStrategy);

        Payment mockPayment = new Payment();
        mockPayment.setId(1L);
        mockPayment.setStatus("PENDING");

        when(mockStrategy.processPayment(order)).thenReturn(mockPayment);
        when(paymentRepository.save(any(Payment.class))).thenReturn(mockPayment);

        // Act
        PaymentDTO result = paymentService.processPayment(order);

        // Assert
        assertNotNull(result);
        assertEquals("PENDING", result.getStatus());
        verify(rabbitTemplate, times(1)).convertAndSend(anyString(), anyString(), any(Payment.class));
    }

    @Test
    void processPayment_withInvalidPaymentMethod_shouldThrowException() {
        OrderDTO order = new OrderDTO();
        order.setId(1L);
        order.setPaymentMethod("INVALID");

        Exception exception = assertThrows(IllegalArgumentException.class, () -> paymentService.processPayment(order));
        assertEquals("No strategy found for payment method: INVALID", exception.getMessage());
    }
  
}
