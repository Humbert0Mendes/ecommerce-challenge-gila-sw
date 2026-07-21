package com.productorder.entrypoint.api.dto.order;

import com.productorder.core.domain.order.OrderItemDomain;

import java.math.BigDecimal;

public record OrderItemResponse(Long productId, int quantity, BigDecimal unitPrice, BigDecimal subtotal) {
    static OrderItemResponse from(OrderItemDomain item) {
        return new OrderItemResponse(item.productId(), item.quantity(), item.unitPrice(), item.subtotal());
    }
}
