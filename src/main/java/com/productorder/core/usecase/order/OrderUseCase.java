package com.productorder.core.usecase.order;

import com.productorder.core.domain.order.OrderDomain;
import com.productorder.core.domain.order.OrderIdempotencyDomain;
import com.productorder.core.domain.order.OrderItemDomain;
import com.productorder.core.domain.payment.PaymentResultDomain;
import com.productorder.core.exception.NotFoundException;
import com.productorder.core.exception.PaymentProcessingException;
import com.productorder.core.gateway.OrderIdempotencyGateway;
import com.productorder.core.gateway.PaymentGateway;
import com.productorder.core.gateway.PageQuery;
import com.productorder.core.gateway.PageResult;
import com.productorder.core.gateway.OrderGateway;
import com.productorder.core.gateway.ProductGateway;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderUseCase extends AbstractOrderUseCase {

    private final OrderPaymentProcessor paymentProcessor;
    private final PaymentGateway paymentGateway;
    private final OrderIdempotencyGateway idempotencyGateway;

    public OrderUseCase(OrderGateway orders, ProductGateway products, OrderPaymentProcessor paymentProcessor, PaymentGateway paymentGateway, OrderIdempotencyGateway idempotencyGateway) {
        super(orders, products);
        this.paymentProcessor = paymentProcessor;
        this.paymentGateway = paymentGateway;
        this.idempotencyGateway = idempotencyGateway;
    }

    public OrderDomain create(OrderDomain order, OrderIdempotencyDomain idempotency) {
        return idempotencyGateway.execute(idempotency, fingerprint(order),
                () -> processPayment(order));
    }

    @Transactional(readOnly = true)
    public OrderDomain get(Long id) {
        return orders.findById(id).orElseThrow(() -> new NotFoundException("Order with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public PageResult<OrderDomain> list(PageQuery q) {
        return orders.findAll(q);
    }

    private OrderDomain processPayment(OrderDomain draft) {
        var order = paymentProcessor.createAndReserve(draft);
        try {
            var processingOrder = paymentProcessor.startProcessing(order.getId());
            PaymentResultDomain payment = paymentGateway.process(processingOrder);
            if (!payment.approved()) {
                throw new PaymentProcessingException(payment.reason());
            }
            return paymentProcessor.confirm(processingOrder.getId());
        } catch (RuntimeException exception) {
            paymentProcessor.declineAndRestore(order.getId());
            throw exception;
        }
    }

    private String fingerprint(OrderDomain order) {
        return order.getItems().stream()
                .collect(java.util.stream.Collectors.groupingBy(OrderItemDomain::productId, java.util.TreeMap::new, java.util.stream.Collectors.summingInt(OrderItemDomain::quantity)))
                .entrySet()
                .stream()
                .map(entry -> entry.getKey() + ":" + entry.getValue())
                .collect(java.util.stream.Collectors.joining("|"));
    }
}
