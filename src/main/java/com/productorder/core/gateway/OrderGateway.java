package com.productorder.core.gateway;

import com.productorder.core.domain.order.OrderDomain;

import java.util.Optional;

public interface OrderGateway {
    OrderDomain create(OrderDomain orderDomain);

    OrderDomain update(OrderDomain orderDomain);

    Optional<OrderDomain> findById(Long id);

    PageResult<OrderDomain> findAll(PageQuery page);
}
