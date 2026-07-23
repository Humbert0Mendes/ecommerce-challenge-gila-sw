package com.productorder.core.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class OrderDomainTest {
    @Test
    void shouldAllowOnlyProcessingToConfirmedTransition() {
        var order = OrderDomain.created();
        order.startProcessing();
        order.confirm();
        assertThat(order.getStatus()).isEqualTo(OrderStatusEnum.CONFIRMED);
        assertThatThrownBy(order::paymentFailed).isInstanceOf(IllegalStateException.class);
    }

    @Test
    void shouldAllowProcessingToPaymentFailed() {
        var order = OrderDomain.created();
        order.startProcessing();
        order.paymentFailed();
        assertThat(order.getStatus()).isEqualTo(OrderStatusEnum.PAYMENT_FAILED);
    }
}
