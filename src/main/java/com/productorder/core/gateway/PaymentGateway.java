package com.productorder.core.gateway;

import com.productorder.core.domain.order.OrderDomain;
import com.productorder.core.domain.payment.PaymentResultDomain;

public interface PaymentGateway {

    PaymentResultDomain process(OrderDomain order);
}
