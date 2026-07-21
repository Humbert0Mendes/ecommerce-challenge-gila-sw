package com.productorder.core.domain.product;

public record ProductImportIssue(long line, String field, String message) { }
