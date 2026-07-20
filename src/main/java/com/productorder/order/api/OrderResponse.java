package com.productorder.order.api;

import com.productorder.order.domain.Order;
import com.productorder.order.domain.OrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(Long id, LocalDateTime createdAt, OrderStatus status, List<OrderItemResponse> items, BigDecimal total) {
    public static OrderResponse from(Order order) { List<OrderItemResponse> items = order.getItems().stream().map(OrderItemResponse::from).toList(); return new OrderResponse(order.getId(), order.getCreatedAt(), order.getStatus(), items, items.stream().map(OrderItemResponse::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add)); }
}
