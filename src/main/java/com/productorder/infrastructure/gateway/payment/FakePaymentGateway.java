package com.productorder.infrastructure.gateway.payment;

import com.productorder.core.domain.order.OrderDomain;
import com.productorder.core.domain.payment.PaymentResultDomain;
import com.productorder.core.gateway.PaymentGateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FakePaymentGateway implements PaymentGateway {

    private final boolean declinePayments;

    public FakePaymentGateway(@Value("${app.payment.fake.decline:false}") boolean declinePayments) {
        this.declinePayments = declinePayments;
    }

    @Override
    public PaymentResultDomain process(OrderDomain order) {
        if (declinePayments) {
            return PaymentResultDomain.failure("Fake payment was declined");
        }
        return PaymentResultDomain.success();
    }
}
