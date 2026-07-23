package com.productorder.core.usecase.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.productorder.core.domain.order.OrderDomain;
import com.productorder.core.domain.order.OrderIdempotencyDomain;
import com.productorder.core.domain.order.OrderStatusEnum;
import com.productorder.core.gateway.OrderGateway;
import com.productorder.core.gateway.OrderIdempotencyGateway;
import com.productorder.core.gateway.PaymentEventPublisher;
import com.productorder.core.gateway.ProductGateway;

import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderUseCaseTest {
    @Mock
    OrderGateway orders;
    @Mock
    ProductGateway products;
    @Mock
    OrderPaymentProcessor orderProcessor;
    @Mock
    PaymentEventPublisher publisher;
    @Mock
    OrderIdempotencyGateway idempotency;

    @Test
    void shouldPublishPaymentRequestedAfterOrderIsProcessing() {
        var useCase = new OrderUseCase(orders, products, orderProcessor, publisher, idempotency);
        var processing = new OrderDomain(1L, null, OrderStatusEnum.PROCESSING, java.util.List.of());

        when(idempotency.execute(any(), anyString(), any())).thenAnswer(invocation -> ((Supplier<OrderDomain>) invocation.getArgument(2)).get());
        when(orderProcessor.createAndReserveProcessing(any())).thenReturn(processing);

        assertThat(useCase.create(OrderDomain.created(), new OrderIdempotencyDomain("user", "key"))).isSameAs(processing);
        verify(publisher).publish(any());
    }
}
