package com.productorder.entrypoint.api.dto.product;

public record ImportError(long line, String field, String message) {
}
