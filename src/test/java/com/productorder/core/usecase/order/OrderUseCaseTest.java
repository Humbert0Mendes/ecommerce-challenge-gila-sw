package com.productorder.core.usecase.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.lenient;

import com.productorder.core.domain.order.OrderDomain;
import com.productorder.core.domain.order.OrderIdempotencyDomain;
import com.productorder.core.domain.order.OrderItemDomain;
import com.productorder.core.domain.order.OrderStatusEnum;
import com.productorder.core.domain.payment.PaymentResultDomain;
import com.productorder.core.exception.NotFoundException;
import com.productorder.core.exception.PaymentProcessingException;
import com.productorder.core.gateway.OrderGateway;
import com.productorder.core.gateway.OrderIdempotencyGateway;
import com.productorder.core.gateway.PageQuery;
import com.productorder.core.gateway.PageResult;
import com.productorder.core.gateway.PaymentGateway;
import com.productorder.core.gateway.ProductGateway;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderUseCaseTest {

    @Mock
    private OrderGateway orders;

    @Mock
    private ProductGateway products;

    @Mock
    private OrderPaymentProcessor paymentProcessor;

    @Mock
    private PaymentGateway paymentGateway;

    @Mock
    private OrderIdempotencyGateway idempotencyGateway;

    private OrderUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new OrderUseCase(orders, products, paymentProcessor, paymentGateway, idempotencyGateway);
        lenient().when(idempotencyGateway.execute(any(), anyString(), any())).thenAnswer(invocation -> ((Supplier<OrderDomain>) invocation.getArgument(2)).get());
    }

    @Test
    void shouldConfirmOrderAfterSuccessfulPayment() {
        OrderDomain draft = draft();
        OrderDomain created = new OrderDomain(1L, null, OrderStatusEnum.CREATED, draft.getItems());
        OrderDomain processing = new OrderDomain(1L, null, OrderStatusEnum.PROCESSING, draft.getItems());
        OrderDomain confirmed = new OrderDomain(1L, null, OrderStatusEnum.CONFIRMED, draft.getItems());
        when(paymentProcessor.createAndReserve(draft)).thenReturn(created);
        when(paymentProcessor.startProcessing(1L)).thenReturn(processing);
        when(paymentGateway.process(processing)).thenReturn(PaymentResultDomain.success());
        when(paymentProcessor.confirm(1L)).thenReturn(confirmed);

        OrderDomain result = useCase.create(draft, new OrderIdempotencyDomain("tester", "key-1"));

        assertThat(result.getStatus()).isEqualTo(OrderStatusEnum.CONFIRMED);
        verify(paymentProcessor).startProcessing(1L);
        verify(paymentProcessor).confirm(1L);
    }

    @Test
    void shouldDeclineOrderAndRestoreStockWhenPaymentIsRejected() {
        OrderDomain draft = draft();
        OrderDomain created = new OrderDomain(1L, null, OrderStatusEnum.CREATED, draft.getItems());
        OrderDomain processing = new OrderDomain(1L, null, OrderStatusEnum.PROCESSING, draft.getItems());
        when(paymentProcessor.createAndReserve(draft)).thenReturn(created);
        when(paymentProcessor.startProcessing(1L)).thenReturn(processing);
        when(paymentGateway.process(processing)).thenReturn(PaymentResultDomain.failure("card declined"));

        assertThatThrownBy(() -> useCase.create(draft, new OrderIdempotencyDomain("tester", "key-1")))
                .isInstanceOf(PaymentProcessingException.class)
                .hasMessage("card declined");

        verify(paymentProcessor).declineAndRestore(1L);
    }

    @Test
    void shouldReturnOrderById() {
        OrderDomain order = OrderDomain.created();
        when(orders.findById(1L)).thenReturn(Optional.of(order));

        assertThat(useCase.get(1L)).isSameAs(order);
    }

    @Test
    void shouldRejectLookupForMissingOrder() {
        when(orders.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.get(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Order with id 1 not found not found");
    }

    @Test
    void shouldReturnOrderPage() {
        PageQuery page = new PageQuery(0, 20, "createdAt", "DESC");
        PageResult<OrderDomain> expected = new PageResult<>(List.of(OrderDomain.created()), 0, 20, 1, 1);
        when(orders.findAll(page)).thenReturn(expected);

        assertThat(useCase.list(page)).isSameAs(expected);
    }

    private OrderDomain draft() {
        return new OrderDomain(null, null, OrderStatusEnum.CREATED, List.of(new OrderItemDomain(1L, 2, new BigDecimal("19.90"))));
    }
}
