package com.productorder.order.api;

import com.productorder.order.domain.OrderItem;
import java.math.BigDecimal;

public record OrderItemResponse(Long productId, int quantity, BigDecimal unitPrice, BigDecimal subtotal) {
    static OrderItemResponse from(OrderItem item) { return new OrderItemResponse(item.getProduct().getId(), item.getQuantity(), item.getUnitPrice(), item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))); }
}
