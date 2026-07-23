package com.productorder.core.gateway;

import com.productorder.core.domain.payment.PaymentRequestedEvent;

public interface PaymentEventPublisher {
    void publish(PaymentRequestedEvent event);
}
