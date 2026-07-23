package com.productorder.infrastructure.messaging;

import com.productorder.core.domain.payment.PaymentRequestedEvent;
import com.productorder.core.gateway.PaymentEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class RabbitPaymentEventPublisher implements PaymentEventPublisher {
    private static final Logger log = LoggerFactory.getLogger(RabbitPaymentEventPublisher.class);
    private final RabbitTemplate rabbitTemplate;
    private final String exchange;
    private final String routingKey;

    public RabbitPaymentEventPublisher(RabbitTemplate rabbitTemplate, @Value("${app.rabbitmq.payment-exchange}") String exchange, @Value("${app.rabbitmq.payment-delay-routing-key}") String routingKey) {
        this.rabbitTemplate = rabbitTemplate;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

    public void publish(PaymentRequestedEvent event) {
        log.info("Publishing payment requested eventId={} orderId={}", event.eventId(), event.orderId());
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
    }
}
