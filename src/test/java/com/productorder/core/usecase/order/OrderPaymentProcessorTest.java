package com.productorder.core.usecase.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.productorder.core.domain.order.OrderDomain;
import com.productorder.core.domain.order.OrderItemDomain;
import com.productorder.core.domain.order.OrderStatusEnum;
import com.productorder.core.domain.product.ProductDomain;
import com.productorder.core.exception.BusinessRuleException;
import com.productorder.core.gateway.OrderGateway;
import com.productorder.core.gateway.ProductGateway;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderPaymentProcessorTest {
    @Mock
    OrderGateway orders;
    @Mock
    ProductGateway products;

    @Test
    void shouldCreateProcessingOrderAndReserveStockAtomically() {
        var processor = new OrderPaymentProcessor(orders, products);
        var product = ProductDomain.create("Mouse", "MOUSE-001", "Mouse", "Peripherals", new BigDecimal("19.90"), 3, new BigDecimal("0.12"));

        when(products.findActiveById(1L)).thenReturn(Optional.of(product));
        when(products.reserveStock(1L, 2)).thenReturn(true);
        when(orders.create(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = processor.createAndReserveProcessing(new OrderDomain(null, null, OrderStatusEnum.CREATED, List.of(new OrderItemDomain(1L, 2, null))));

        assertThat(result.getStatus()).isEqualTo(OrderStatusEnum.PROCESSING);
        verify(products).reserveStock(1L, 2);
    }

    @Test
    void shouldRollbackCreationWhenAtomicReservationFails() {
        var processor = new OrderPaymentProcessor(orders, products);
        var product = ProductDomain.create("Mouse", "MOUSE-001", "Mouse", "Peripherals", new BigDecimal("19.90"), 1, new BigDecimal("0.12"));

        when(products.findActiveById(1L)).thenReturn(Optional.of(product));
        when(products.reserveStock(1L, 2)).thenReturn(false);

        assertThatThrownBy(() -> processor.createAndReserveProcessing(new OrderDomain(null, null, OrderStatusEnum.CREATED, List.of(new OrderItemDomain(1L, 2, null))))).isInstanceOf(BusinessRuleException.class);
    }
}
