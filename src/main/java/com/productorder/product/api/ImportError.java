package com.productorder.product.api;

public record ImportError(long line, String field, String message) { }
