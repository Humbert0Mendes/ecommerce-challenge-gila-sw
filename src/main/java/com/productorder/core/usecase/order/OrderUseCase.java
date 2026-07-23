package com.productorder.core.usecase.order;

import com.productorder.core.domain.order.OrderDomain;
import com.productorder.core.domain.order.OrderFilterDomain;
import com.productorder.core.domain.order.OrderIdempotencyDomain;
import com.productorder.core.domain.order.OrderItemDomain;
import com.productorder.core.domain.payment.PaymentRequestedEvent;
import com.productorder.core.exception.NotFoundException;
import com.productorder.core.gateway.OrderGateway;
import com.productorder.core.gateway.OrderIdempotencyGateway;
import com.productorder.core.gateway.PageQuery;
import com.productorder.core.gateway.PageResult;
import com.productorder.core.gateway.PaymentEventPublisher;
import com.productorder.core.gateway.ProductGateway;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderUseCase extends AbstractOrderUseCase {
    private final OrderPaymentProcessor orderProcessor;
    private final PaymentEventPublisher eventPublisher;
    private final OrderIdempotencyGateway idempotencyGateway;

    public OrderUseCase(OrderGateway orders, ProductGateway products, OrderPaymentProcessor orderProcessor, PaymentEventPublisher eventPublisher, OrderIdempotencyGateway idempotencyGateway) {
        super(orders, products);
        this.orderProcessor = orderProcessor;
        this.eventPublisher = eventPublisher;
        this.idempotencyGateway = idempotencyGateway;
    }

    public OrderDomain create(OrderDomain order, OrderIdempotencyDomain idempotency) {
        return idempotencyGateway.execute(idempotency, fingerprint(order), () -> {
            var processing = orderProcessor.createAndReserveProcessing(order);
            eventPublisher.publish(PaymentRequestedEvent.forOrder(processing.getId(), processing.total()));
            return processing;
        });
    }

    @Transactional(readOnly = true)
    public OrderDomain get(Long id) {
        return orders.findById(id).orElseThrow(() -> new NotFoundException("Order with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public PageResult<OrderDomain> list(OrderFilterDomain filter, PageQuery q) {
        return orders.findAll(filter, q);
    }

    @Transactional(readOnly = true)
    public PageResult<OrderDomain> list(PageQuery q) {
        return orders.findAll(q);
    }

    private String fingerprint(OrderDomain order) {
        return order.getItems().stream().collect(java.util.stream.Collectors.groupingBy(OrderItemDomain::productId, java.util.TreeMap::new, java.util.stream.Collectors.summingInt(OrderItemDomain::quantity))).entrySet().stream().map(entry -> entry.getKey() + ":" + entry.getValue()).collect(java.util.stream.Collectors.joining("|"));
    }
}
