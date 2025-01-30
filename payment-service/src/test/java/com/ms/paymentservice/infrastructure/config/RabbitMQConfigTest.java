package com.ms.paymentservice.infrastructure.config;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import org.springframework.amqp.support.converter.MessageConverter;

class RabbitMQConfigTest {

    private RabbitMQConfig rabbitMQConfig;

    @BeforeEach
    void setUp() {
        rabbitMQConfig = new RabbitMQConfig();
    }

    @Test
    void shouldCreateQueuesSuccessfully() {
        Queue orderQueue = rabbitMQConfig.orderQueue();
        assertNotNull(orderQueue);
        assertEquals("order.queue", orderQueue.getName());
        assertTrue(orderQueue.isDurable());

        Queue paymentOrderQueue = rabbitMQConfig.paymentOrderQueue();
        assertNotNull(paymentOrderQueue);
        assertEquals("payment.order.queue", paymentOrderQueue.getName());
        assertTrue(paymentOrderQueue.isDurable());

        Queue paymentStockQueue = rabbitMQConfig.paymentStockQueue();
        assertNotNull(paymentStockQueue);
        assertEquals("payment.stock.queue", paymentStockQueue.getName());
        assertTrue(paymentStockQueue.isDurable());
    }

    @Test
    void shouldCreateExchangesSuccessfully() {
        TopicExchange orderExchange = rabbitMQConfig.orderExchange();
        assertNotNull(orderExchange);
        assertEquals("order.exchange", orderExchange.getName());

        FanoutExchange paymentExchange = rabbitMQConfig.paymentExchange();
        assertNotNull(paymentExchange);
        assertEquals("payment.exchange", paymentExchange.getName());
    }

    @Test
    void shouldCreateBindingsSuccessfully() {
        Binding orderBinding = rabbitMQConfig.orderBinding();
        assertNotNull(orderBinding);
        assertEquals("order.routingkey", orderBinding.getRoutingKey());
        assertEquals("order.exchange", orderBinding.getExchange());
        assertEquals("order.queue", orderBinding.getDestination());

        Binding orderPaymentBinding = rabbitMQConfig.orderPaymentBinding();
        assertNotNull(orderPaymentBinding);
        assertEquals("payment.exchange", orderPaymentBinding.getExchange());
        assertEquals("payment.order.queue", orderPaymentBinding.getDestination());

        Binding stockPaymentBinding = rabbitMQConfig.stockPaymentBinding();
        assertNotNull(stockPaymentBinding);
        assertEquals("payment.exchange", stockPaymentBinding.getExchange());
        assertEquals("payment.stock.queue", stockPaymentBinding.getDestination());
    }

    @Test
    void shouldCreateJsonMessageConverterSuccessfully() {
        MessageConverter converter = (MessageConverter) rabbitMQConfig.jsonMessageConverter();
        assertNotNull(converter);
        assertTrue(converter instanceof Jackson2JsonMessageConverter);
    }
}
