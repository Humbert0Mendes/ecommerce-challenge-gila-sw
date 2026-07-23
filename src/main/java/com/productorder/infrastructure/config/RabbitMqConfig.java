package com.productorder.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    @Bean
    Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    DirectExchange paymentExchange(@org.springframework.beans.factory.annotation.Value("${app.rabbitmq.payment-exchange}") String name) {
        return new DirectExchange(name);
    }

    @Bean
    DirectExchange paymentDeadLetterExchange(@org.springframework.beans.factory.annotation.Value("${app.rabbitmq.payment-exchange}") String name) {
        return new DirectExchange(name + ".dlx");
    }

    @Bean
    Queue paymentRequestedQueue(@org.springframework.beans.factory.annotation.Value("${app.rabbitmq.payment-requested-queue}") String name, @org.springframework.beans.factory.annotation.Value("${app.rabbitmq.payment-exchange}") String exchange, @org.springframework.beans.factory.annotation.Value("${app.rabbitmq.payment-requested-dlq-routing-key}") String key) {
        return org.springframework.amqp.core.QueueBuilder.durable(name).deadLetterExchange(exchange + ".dlx").deadLetterRoutingKey(key).build();
    }

    @Bean
    Queue paymentRequestedDlq(@org.springframework.beans.factory.annotation.Value("${app.rabbitmq.payment-requested-dlq}") String name) {
        return org.springframework.amqp.core.QueueBuilder.durable(name).build();
    }

    @Bean
    Queue paymentDelayQueue(@org.springframework.beans.factory.annotation.Value("${app.rabbitmq.payment-delay-queue}") String name, @org.springframework.beans.factory.annotation.Value("${app.rabbitmq.payment-exchange}") String exchange, @org.springframework.beans.factory.annotation.Value("${app.rabbitmq.payment-requested-routing-key}") String routingKey, @org.springframework.beans.factory.annotation.Value("${app.rabbitmq.payment-delay-ms}") int delayMs) {
        return org.springframework.amqp.core.QueueBuilder.durable(name).ttl(delayMs).deadLetterExchange(exchange).deadLetterRoutingKey(routingKey).build();
    }

    @Bean
    Binding paymentRequestedBinding(Queue paymentRequestedQueue, DirectExchange paymentExchange, @org.springframework.beans.factory.annotation.Value("${app.rabbitmq.payment-requested-routing-key}") String key) {
        return BindingBuilder.bind(paymentRequestedQueue).to(paymentExchange).with(key);
    }

    @Bean
    Binding paymentDelayBinding(Queue paymentDelayQueue, DirectExchange paymentExchange, @org.springframework.beans.factory.annotation.Value("${app.rabbitmq.payment-delay-routing-key}") String key) {
        return BindingBuilder.bind(paymentDelayQueue).to(paymentExchange).with(key);
    }

    @Bean
    Binding paymentRequestedDlqBinding(Queue paymentRequestedDlq, DirectExchange paymentDeadLetterExchange, @org.springframework.beans.factory.annotation.Value("${app.rabbitmq.payment-requested-dlq-routing-key}") String key) {
        return BindingBuilder.bind(paymentRequestedDlq).to(paymentDeadLetterExchange).with(key);
    }

    @Bean
    SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(
            ConnectionFactory connectionFactory,
            Jackson2JsonMessageConverter messageConverter) {

        var factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(messageConverter);
        return factory;
    }
}
