package com.productorder.core.domain.order;

public record OrderIdempotencyDomain(String subject, String key) {
}
