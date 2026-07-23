package com.productorder.infrastructure.gateway.idempotency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.productorder.core.domain.order.OrderDomain;
import com.productorder.core.domain.order.OrderIdempotencyDomain;
import com.productorder.core.exception.IdempotencyConflictException;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

class LocalOrderIdempotencyGatewayTest {

    private final LocalOrderIdempotencyGateway gateway = new LocalOrderIdempotencyGateway(Duration.ofHours(24));

    @Test
    void shouldReturnCachedOrderWithoutExecutingActionTwice() {
        var idempotency = new OrderIdempotencyDomain("tester", "key-1");
        var order = OrderDomain.created();
        var executions = new AtomicInteger();

        var first = gateway.execute(idempotency, "1:2", () -> {
            executions.incrementAndGet();
            return order;
        });
        var duplicate = gateway.execute(idempotency, "1:2", () -> {
            executions.incrementAndGet();
            return OrderDomain.created();
        });

        assertThat(first).isSameAs(order);
        assertThat(duplicate).isSameAs(order);
        assertThat(executions).hasValue(1);
    }

    @Test
    void shouldRejectSameKeyWithDifferentRequestFingerprint() {
        var idempotency = new OrderIdempotencyDomain("tester", "key-1");
        gateway.execute(idempotency, "1:2", OrderDomain::created);

        assertThatThrownBy(() -> gateway.execute(idempotency, "1:3", OrderDomain::created))
                .isInstanceOf(IdempotencyConflictException.class);
    }

    @Test
    void shouldReuseFailureWithoutExecutingActionAgain() {
        var idempotency = new OrderIdempotencyDomain("tester", "key-1");
        AtomicInteger executions = new AtomicInteger();

        assertThatThrownBy(() -> gateway.execute(idempotency, "1:2", () -> {
            executions.incrementAndGet();
            throw new IllegalStateException("payment unavailable");
        })).isInstanceOf(IllegalStateException.class);

        assertThatThrownBy(() -> gateway.execute(idempotency, "1:2", () -> {
            executions.incrementAndGet();
            return OrderDomain.created();
        })).isInstanceOf(IllegalStateException.class)
                .hasMessage("payment unavailable");

        assertThat(executions).hasValue(1);
    }
}
