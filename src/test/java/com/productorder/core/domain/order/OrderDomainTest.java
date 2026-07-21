package com.productorder.core.domain.order;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

class OrderDomainTest {

    @Test
    void shouldCreateOrderAndCalculateTotalFromItems() {
        OrderDomain order = OrderDomain.created();

        order.addItem(new OrderItemDomain(1L, 2, new BigDecimal("19.90")));
        order.addItem(new OrderItemDomain(2L, 1, new BigDecimal("5.00")));

        assertThat(order.getStatus()).isEqualTo(OrderStatusEnum.CREATED);
        assertThat(order.getItems()).hasSize(2);
        assertThat(order.total()).isEqualByComparingTo("44.80");
    }

    @Test
    void shouldTransitionOrderThroughPaymentStatuses() {
        OrderDomain order = OrderDomain.created();

        order.startProcessing();
        assertThat(order.getStatus()).isEqualTo(OrderStatusEnum.PROCESSING);

        order.confirm();
        assertThat(order.getStatus()).isEqualTo(OrderStatusEnum.CONFIRMED);

        order.decline();
        assertThat(order.getStatus()).isEqualTo(OrderStatusEnum.DECLINED);
    }

    @Test
    void shouldExposeOrderIdentifierAndCreationDate() {
        LocalDateTime createdAt = LocalDateTime.of(2026, 7, 21, 10, 0);
        OrderDomain order = new OrderDomain(10L, createdAt, OrderStatusEnum.CONFIRMED, List.of());

        assertThat(order.getId()).isEqualTo(10L);
        assertThat(order.getCreatedAt()).isEqualTo(createdAt);
    }
}
