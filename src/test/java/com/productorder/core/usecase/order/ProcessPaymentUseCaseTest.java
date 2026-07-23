package com.productorder.core.usecase.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.productorder.core.domain.order.OrderDomain;
import com.productorder.core.domain.order.OrderItemDomain;
import com.productorder.core.domain.order.OrderStatusEnum;
import com.productorder.core.domain.payment.PaymentRequestedEvent;
import com.productorder.core.domain.payment.PaymentResultDomain;
import com.productorder.core.gateway.OrderGateway;
import com.productorder.core.gateway.PaymentGateway;
import com.productorder.core.gateway.ProcessedEventGateway;
import com.productorder.core.gateway.ProductGateway;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProcessPaymentUseCaseTest {
    @Mock
    OrderGateway orders;
    @Mock
    ProductGateway products;
    @Mock
    PaymentGateway paymentGateway;
    @Mock
    ProcessedEventGateway processedEvents;

    private ProcessPaymentUseCase useCase() {
        return new ProcessPaymentUseCase(orders, products, paymentGateway, processedEvents);
    }

    private PaymentRequestedEvent event() {
        return PaymentRequestedEvent.forOrder(1L, new BigDecimal("39.80"));
    }

    private OrderDomain processing() {
        return new OrderDomain(1L, null, OrderStatusEnum.PROCESSING, List.of(new OrderItemDomain(2L, "Mouse", 2, new BigDecimal("19.90"))));
    }

    @Test
    void shouldConfirmApprovedPayment() {
        var event = event();
        var order = processing();
        when(processedEvents.register(event.eventId(), "PAYMENT_REQUESTED")).thenReturn(true);
        when(orders.findById(1L)).thenReturn(Optional.of(order));
        when(paymentGateway.process(order)).thenReturn(PaymentResultDomain.success());

        useCase().execute(event);

        assertThat(order.getStatus()).isEqualTo(OrderStatusEnum.CONFIRMED);
        verify(orders).update(order);
        verify(products, never()).releaseStock(2L, 2);
    }

    @Test
    void shouldFailPaymentAndReleaseStock() {
        var event = event();
        var order = processing();

        when(processedEvents.register(event.eventId(), "PAYMENT_REQUESTED")).thenReturn(true);
        when(orders.findById(1L)).thenReturn(Optional.of(order));
        when(paymentGateway.process(order)).thenReturn(PaymentResultDomain.failure("declined"));

        useCase().execute(event);

        assertThat(order.getStatus()).isEqualTo(OrderStatusEnum.PAYMENT_FAILED);
        verify(products).releaseStock(2L, 2);
        verify(orders).update(order);
    }

    @Test
    void shouldIgnoreDuplicateEvent() {
        var event = event();

        when(processedEvents.register(event.eventId(), "PAYMENT_REQUESTED")).thenReturn(false);

        useCase().execute(event);

        verify(orders, never()).findById(1L);
        verify(paymentGateway, never()).process(org.mockito.ArgumentMatchers.any());
    }
}
