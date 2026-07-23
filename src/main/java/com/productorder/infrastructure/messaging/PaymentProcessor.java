package com.productorder.infrastructure.messaging;

import com.productorder.core.domain.payment.PaymentRequestedEvent;
import com.productorder.core.usecase.order.ProcessPaymentUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class PaymentProcessor {
    private static final Logger log = LoggerFactory.getLogger(PaymentProcessor.class);
    private final ProcessPaymentUseCase processPaymentUseCase;

    public PaymentProcessor(ProcessPaymentUseCase processPaymentUseCase) {
        this.processPaymentUseCase = processPaymentUseCase;
    }

    @RabbitListener(queues = "${app.rabbitmq.payment-requested-queue}")
    public void process(PaymentRequestedEvent event) {
        log.info("Received payment event eventId={} orderId={}", event.eventId(), event.orderId());
        processPaymentUseCase.execute(event);
    }
}
