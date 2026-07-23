package com.productorder.dataprovider.gateway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.productorder.core.domain.order.OrderDomain;
import com.productorder.core.domain.order.OrderStatusEnum;
import com.productorder.dataprovider.entity.OrderEntity;
import com.productorder.dataprovider.mapper.OrderEntityMapper;
import com.productorder.dataprovider.repository.OrderJpaRepository;
import com.productorder.dataprovider.repository.ProductJpaRepository;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderDatabaseGatewayTest {

    @Mock
    private OrderJpaRepository orders;

    @Mock
    private ProductJpaRepository products;

    @Mock
    private OrderEntityMapper mapper;

    @Mock
    private OrderEntity entity;

    private OrderDatabaseGateway gateway;

    @BeforeEach
    void setUp() {
        gateway = new OrderDatabaseGateway(orders, products, mapper);
    }

    @Test
    void shouldCreateNewEntityWhenOrderHasNoIdentifier() {
        var draft = new OrderDomain(null, null, OrderStatusEnum.CREATED, List.of());
        when(mapper.toEntity(eq(draft), any())).thenReturn(entity);
        when(orders.save(entity)).thenReturn(entity);
        when(mapper.toDomain(entity)).thenReturn(draft);

        OrderDomain result = gateway.create(draft);

        assertThat(result).isSameAs(draft);
        verify(orders).save(entity);
        verify(orders, never()).findById(any());
    }

    @Test
    void shouldUpdateExistingEntityWhenOrderHasIdentifier() {
        var order = new OrderDomain(1L, null, OrderStatusEnum.PROCESSING, List.of());

        var result = gateway.update(order);

        assertThat(result).isSameAs(order);
        verify(orders).updateStatus(1L, OrderStatusEnum.PROCESSING);
        verify(mapper, never()).toEntity(any(), any());
        verify(orders, never()).findById(any());
        verify(orders, never()).save(any());
    }
}
