package com.productorder.infrastructure.messaging;

import static org.mockito.Mockito.verify;

import com.productorder.core.domain.payment.PaymentRequestedEvent;
import com.productorder.core.usecase.order.ProcessPaymentUseCase;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PaymentProcessorTest {
    @Test
    void shouldDelegateMessageToUseCase() {
        var useCase = Mockito.mock(ProcessPaymentUseCase.class);
        var event = PaymentRequestedEvent.forOrder(1L, new BigDecimal("10.00"));
        new PaymentProcessor(useCase).process(event);
        verify(useCase).execute(event);
    }
}
