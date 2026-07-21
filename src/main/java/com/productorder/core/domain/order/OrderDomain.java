package com.productorder.core.domain.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDomain {

    private Long id;
    private LocalDateTime createdAt;
    private OrderStatusEnum status;
    private final List<OrderItemDomain> items;

    public OrderDomain(Long id, LocalDateTime createdAt, OrderStatusEnum status, List<OrderItemDomain> items) {
        this.id = id;
        this.createdAt = createdAt;
        this.status = status;
        this.items = new ArrayList<>(items);
    }

    public static OrderDomain pending() {
        return new OrderDomain(null, null, OrderStatusEnum.PENDING, List.of());
    }

    public void addItem(OrderItemDomain item) {
        items.add(item);
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public OrderStatusEnum getStatus() {
        return status;
    }

    public List<OrderItemDomain> getItems() {
        return List.copyOf(items);
    }

    public BigDecimal total() {
        return items.stream().map(OrderItemDomain::subtotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
