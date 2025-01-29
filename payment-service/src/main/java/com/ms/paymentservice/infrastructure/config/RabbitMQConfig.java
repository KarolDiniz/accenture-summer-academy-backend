package com.ms.paymentservice.infrastructure.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue orderQueue() {
        return new Queue("order.queue", true);
    }

    @Bean
    public Queue paymentOrderQueue() {
        return new Queue("payment.order.queue", true);
    }

    @Bean
    public Queue paymentStockQueue() {
        return new Queue("payment.stock.queue", true);
    }

    @Bean
    public TopicExchange orderExchange() {
        return new TopicExchange("order.exchange");
    }


    @Bean
    public FanoutExchange paymentExchange() {
        return new FanoutExchange("payment.exchange");
    }

    @Bean
    public Binding orderBinding() {
        return BindingBuilder
                .bind(orderQueue())
                .to(orderExchange())
                .with("order.routingkey");
    }

    // Bindings para as novas queues usando FanoutExchange
    @Bean
    public Binding orderPaymentBinding() {
        return BindingBuilder
                .bind(paymentOrderQueue())
                .to(paymentExchange());
    }

    @Bean
    public Binding stockPaymentBinding() {
        return BindingBuilder
                .bind(paymentStockQueue())
                .to(paymentExchange());
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}