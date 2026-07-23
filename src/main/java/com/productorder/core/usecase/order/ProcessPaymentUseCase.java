package com.productorder.core.usecase.order;

import com.productorder.core.domain.order.OrderStatusEnum;
import com.productorder.core.domain.payment.PaymentRequestedEvent;
import com.productorder.core.gateway.OrderGateway;
import com.productorder.core.gateway.PaymentGateway;
import com.productorder.core.gateway.ProcessedEventGateway;
import com.productorder.core.gateway.ProductGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProcessPaymentUseCase {
    private static final Logger log = LoggerFactory.getLogger(ProcessPaymentUseCase.class);
    private static final String EVENT_TYPE = "PAYMENT_REQUESTED";
    private final OrderGateway orders;
    private final ProductGateway products;
    private final PaymentGateway paymentGateway;
    private final ProcessedEventGateway processedEvents;

    public ProcessPaymentUseCase(OrderGateway orders, ProductGateway products, PaymentGateway paymentGateway, ProcessedEventGateway processedEvents) {
        this.orders = orders;
        this.products = products;
        this.paymentGateway = paymentGateway;
        this.processedEvents = processedEvents;
    }

    @Transactional
    public void execute(PaymentRequestedEvent event) {
        if (!processedEvents.register(event.eventId(), EVENT_TYPE)) {
            log.info("Ignoring duplicate payment event eventId={} orderId={}", event.eventId(), event.orderId());
            return;
        }

        var order = orders.findById(event.orderId()).orElseThrow(
                () -> new IllegalStateException("Order " + event.orderId() + " was not found"));

        if (order.getStatus() != OrderStatusEnum.PROCESSING) {
            log.info("Ignoring payment event eventId={} orderId={} status={}", event.eventId(), event.orderId(), order.getStatus());
            return;
        }
        var result = paymentGateway.process(order);
        if (result.approved()) {
            order.confirm();
            orders.update(order);
            log.info("Payment approved eventId={} orderId={} transactionId={}", event.eventId(), event.orderId(), result.transactionId());
            return;
        }
        order.getItems().forEach(item -> products.releaseStock(item.productId(), item.quantity()));
        order.paymentFailed();
        orders.update(order);
        log.warn("Payment declined eventId={} orderId={} reason={}", event.eventId(), event.orderId(), result.failureReason());
    }
}
