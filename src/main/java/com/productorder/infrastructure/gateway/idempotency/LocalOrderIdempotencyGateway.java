package com.productorder.infrastructure.gateway.idempotency;

import com.productorder.core.domain.order.OrderDomain;
import com.productorder.core.domain.order.OrderIdempotencyDomain;
import com.productorder.core.exception.IdempotencyConflictException;
import com.productorder.core.gateway.OrderIdempotencyGateway;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class LocalOrderIdempotencyGateway implements OrderIdempotencyGateway {

    private final ConcurrentHashMap<String, CacheEntry> entries = new ConcurrentHashMap<>();
    private final Duration ttl;

    public LocalOrderIdempotencyGateway(@Value("${app.idempotency.ttl:PT24H}") Duration ttl) {
        this.ttl = ttl;
    }

    @Override
    public OrderDomain execute(OrderIdempotencyDomain idempotency, String requestFingerprint, Supplier<OrderDomain> action) {
        String cacheKey = idempotency.subject() + ":" + idempotency.key();
        CacheEntry newEntry = new CacheEntry(requestFingerprint, Instant.now().plus(ttl));

        while (true) {
            CacheEntry existing = entries.putIfAbsent(cacheKey, newEntry);
            if (existing == null) {
                return executeAction(newEntry, action);
            }
            if (existing.expiresAt().isBefore(Instant.now())) {
                entries.remove(cacheKey, existing);
                continue;
            }
            if (!existing.requestFingerprint().equals(requestFingerprint)) {
                throw new IdempotencyConflictException();
            }
            return await(existing);
        }
    }

    private OrderDomain executeAction(CacheEntry entry, Supplier<OrderDomain> action) {
        try {
            OrderDomain order = action.get();
            entry.result().complete(order);
            return order;
        } catch (RuntimeException exception) {
            entry.result().completeExceptionally(exception);
            throw exception;
        }
    }

    private OrderDomain await(CacheEntry entry) {
        try {
            return entry.result().join();
        } catch (CompletionException exception) {
            if (exception.getCause() instanceof RuntimeException runtimeException) {
                throw runtimeException;
            }
            throw exception;
        }
    }

    private record CacheEntry(String requestFingerprint, Instant expiresAt, CompletableFuture<OrderDomain> result) {

        private CacheEntry(String requestFingerprint, Instant expiresAt) {
            this(requestFingerprint, expiresAt, new CompletableFuture<>());
        }
    }
}
