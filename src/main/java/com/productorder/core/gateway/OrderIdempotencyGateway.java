package com.productorder.core.gateway;

import com.productorder.core.domain.order.OrderDomain;
import com.productorder.core.domain.order.OrderIdempotencyDomain;

import java.util.function.Supplier;

public interface OrderIdempotencyGateway {

    OrderDomain execute(OrderIdempotencyDomain idempotency, String requestFingerprint, Supplier<OrderDomain> action);
}
