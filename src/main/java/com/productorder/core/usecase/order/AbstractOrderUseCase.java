package com.productorder.core.usecase.order;

import com.productorder.core.gateway.OrderGateway;
import com.productorder.core.gateway.ProductGateway;

public abstract class AbstractOrderUseCase {
    protected final OrderGateway orders;
    protected final ProductGateway products;

    protected AbstractOrderUseCase(OrderGateway orders, ProductGateway products) {
        this.orders = orders;
        this.products = products;
    }
}
