package com.productorder.infrastructure.gateway.payment;

import static org.assertj.core.api.Assertions.assertThat;

import com.productorder.core.domain.order.OrderDomain;
import com.productorder.core.domain.payment.PaymentResultDomain;

import org.junit.jupiter.api.Test;

class FakePaymentGatewayTest {

    @Test
    void shouldApprovePaymentByDefault() {
        var gateway = new FakePaymentGateway(false);

        var result = gateway.process(OrderDomain.created());

        assertThat(result.approved()).isTrue();
        assertThat(result.reason()).isNull();
    }

    @Test
    void shouldDeclinePaymentWhenConfigured() {
        var gateway = new FakePaymentGateway(true);

        PaymentResultDomain result = gateway.process(OrderDomain.created());

        assertThat(result.approved()).isFalse();
        assertThat(result.reason()).isEqualTo("Fake payment was declined");
    }
}
