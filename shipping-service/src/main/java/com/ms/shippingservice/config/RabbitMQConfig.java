package com.ms.shippingservice.config;


import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue stockQueue() {
        return new Queue("stock.queue", true);
    }

    @Bean
    public Queue shippingQueue() {
        return new Queue("shipping.queue", true);
    }

    @Bean
    public TopicExchange shippingExchange() {
        return new TopicExchange("shipping.exchange");
    }

    @Bean
    public Binding shippingBinding() {
        return BindingBuilder
                .bind(shippingQueue())
                .to(shippingExchange())
                .with("shipping.routingkey");
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(messageConverter());
        return template;
    }
}