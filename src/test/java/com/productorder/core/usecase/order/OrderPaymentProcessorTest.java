package com.productorder.core.usecase.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import com.productorder.core.domain.order.OrderDomain;
import com.productorder.core.domain.order.OrderItemDomain;
import com.productorder.core.domain.order.OrderStatusEnum;
import com.productorder.core.domain.product.ProductDomain;
import com.productorder.core.gateway.OrderGateway;
import com.productorder.core.gateway.ProductGateway;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderPaymentProcessorTest {

    @Mock
    private OrderGateway orders;

    @Mock
    private ProductGateway products;

    @Captor
    private ArgumentCaptor<OrderDomain> orderCaptor;

    private OrderPaymentProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new OrderPaymentProcessor(orders, products);
    }

    @Test
    void shouldCreateOrderWithCreatedStatusAndReserveStock() {
        ProductDomain product = product(3);
        OrderDomain draft = new OrderDomain(null, null, OrderStatusEnum.CREATED, List.of(new OrderItemDomain(1L, 2, null)));
        when(products.findActiveByIdForUpdate(1L)).thenReturn(Optional.of(product));
        when(orders.save(any(OrderDomain.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderDomain created = processor.createAndReserve(draft);

        assertThat(created.getStatus()).isEqualTo(OrderStatusEnum.CREATED);
        assertThat(created.getItems()).containsExactly(new OrderItemDomain(1L, 2, new BigDecimal("19.90")));
        assertThat(product.getStock()).isOne();
        verify(products).save(product);
    }

    @Test
    void shouldDeclineOrderAndRestoreReservedStock() {
        ProductDomain product = product(8);
        OrderDomain processing = new OrderDomain(1L, null, OrderStatusEnum.PROCESSING, List.of(new OrderItemDomain(1L, 2, new BigDecimal("19.90"))));
        when(orders.findById(1L)).thenReturn(Optional.of(processing));
        when(products.findByIdForUpdate(1L)).thenReturn(Optional.of(product));

        processor.declineAndRestore(1L);

        assertThat(product.getStock()).isEqualTo(10);
        assertThat(processing.getStatus()).isEqualTo(OrderStatusEnum.DECLINED);
        verify(products).save(product);
        verify(orders).save(orderCaptor.capture());
        assertThat(orderCaptor.getValue()).isSameAs(processing);
    }

    @Test
    void shouldUpdateOrderToProcessingAndConfirmed() {
        OrderDomain order = OrderDomain.created();
        when(orders.findById(1L)).thenReturn(Optional.of(order));
        when(orders.save(any(OrderDomain.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderDomain processing = processor.startProcessing(1L);
        assertThat(processing.getStatus()).isEqualTo(OrderStatusEnum.PROCESSING);

        OrderDomain confirmed = processor.confirm(1L);
        assertThat(confirmed.getStatus()).isEqualTo(OrderStatusEnum.CONFIRMED);
        verify(orders, times(2)).save(order);
    }

    @Test
    void shouldNotRestoreStockTwiceForDeclinedOrder() {
        ProductDomain product = product(8);
        OrderDomain declined = new OrderDomain(1L, null, OrderStatusEnum.DECLINED, List.of(new OrderItemDomain(1L, 2, new BigDecimal("19.90"))));
        when(orders.findById(1L)).thenReturn(Optional.of(declined));

        processor.declineAndRestore(1L);

        assertThat(product.getStock()).isEqualTo(8);
        verify(products, org.mockito.Mockito.never()).findByIdForUpdate(1L);
        verify(products, org.mockito.Mockito.never()).save(product);
    }

    private ProductDomain product(int stock) {
        return ProductDomain.create("Mouse", "MOUSE-001", "Mouse sem fio", "Perifericos", new BigDecimal("19.90"), stock, new BigDecimal("0.120"));
    }
}
