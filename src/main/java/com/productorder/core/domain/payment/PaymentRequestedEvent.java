package com.productorder.core.domain.payment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentRequestedEvent(UUID eventId, Long orderId, BigDecimal totalAmount, Instant createdAt) {
    public static PaymentRequestedEvent forOrder(Long orderId, BigDecimal totalAmount) {
        return new PaymentRequestedEvent(UUID.randomUUID(), orderId, totalAmount, Instant.now());
    }
}
