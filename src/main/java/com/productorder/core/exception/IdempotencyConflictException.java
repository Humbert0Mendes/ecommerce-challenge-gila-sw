package com.productorder.core.exception;

public class IdempotencyConflictException extends RuntimeException {

    public IdempotencyConflictException() {
        super("Idempotency key has already been used with a different request");
    }
}
