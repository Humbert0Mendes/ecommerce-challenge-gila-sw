package com.productorder.dataprovider.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.productorder.core.domain.order.OrderDomain;
import com.productorder.core.domain.order.OrderItemDomain;
import com.productorder.core.domain.order.OrderStatusEnum;
import com.productorder.dataprovider.entity.OrderEntity;
import com.productorder.dataprovider.entity.ProductEntity;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;

class OrderEntityMapperTest {

    private final OrderEntityMapper mapper = new OrderEntityMapper();

    @Test
    void shouldMapOrderTotalToTotalPrice() {
        OrderDomain order = new OrderDomain(null, null, OrderStatusEnum.CREATED, List.of(
                new OrderItemDomain(1L, 2, new BigDecimal("19.90")),
                new OrderItemDomain(2L, 1, new BigDecimal("5.00"))));

        OrderEntity entity = mapper.toEntity(order, id -> new ProductEntity("Product", "SKU-" + id, "Description", "Category", BigDecimal.ONE, 1, BigDecimal.ONE, true));

        assertThat(entity.getStatus()).isEqualTo(OrderStatusEnum.CREATED);
        assertThat(entity.getTotalPrice()).isEqualByComparingTo("44.80");
        assertThat(entity.getItems()).hasSize(2);
    }
}
