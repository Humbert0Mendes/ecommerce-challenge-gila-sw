package com.productorder.core.domain.product;

public record ProductImportRow(long line, String name, String sku, String description, String category, String price, String stock, String weightKg) { }
