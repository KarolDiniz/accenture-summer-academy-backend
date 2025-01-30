package com.ecommerce.notificationservice.infrastructure.config;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class RabbitMQConfigTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void testOrderConfirmedQueueBean() {
        Queue queue = context.getBean("orderConfirmedQueue", Queue.class);
        assertNotNull(queue);
    }

    @Test
    public void testOrderExchangeBean() {
        TopicExchange exchange = context.getBean("orderExchange", TopicExchange.class);
        assertNotNull(exchange);
    }

    @Test
    public void testOrderBindingBean() {
        Binding binding = context.getBean("orderBinding", Binding.class);
        assertNotNull(binding);
    }

    @Test
    public void testJsonMessageConverterBean() {
        Jackson2JsonMessageConverter converter = context.getBean("jsonMessageConverter", Jackson2JsonMessageConverter.class);
        assertNotNull(converter);
    }
}