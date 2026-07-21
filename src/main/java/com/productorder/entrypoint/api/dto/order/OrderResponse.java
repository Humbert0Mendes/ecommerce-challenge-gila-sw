package com.productorder.entrypoint.api.dto.order;

import com.productorder.entrypoint.api.dto.order.OrderItemResponse;

import com.productorder.core.domain.order.OrderDomain;
import com.productorder.core.domain.order.OrderStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(Long id, LocalDateTime createdAt, OrderStatusEnum status, List<OrderItemResponse> items,
                            BigDecimal total) {
    public static OrderResponse from(OrderDomain orderDomain) {
        List<OrderItemResponse> items = orderDomain.getItems().stream().map(OrderItemResponse::from).toList();
        return new OrderResponse(orderDomain.getId(), orderDomain.getCreatedAt(), orderDomain.getStatus(), items, orderDomain.total());
    }
}
