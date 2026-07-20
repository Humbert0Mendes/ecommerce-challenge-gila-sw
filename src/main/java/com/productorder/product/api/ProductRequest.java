package com.productorder.product.api;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank @Size(max = 255) String name,
        @NotBlank @Size(max = 20) String sku,
        @NotBlank String description,
        @NotBlank @Size(max = 100) String category,
        @DecimalMin("0.00") BigDecimal price,
        @Min(0) int stock,
        @DecimalMin("0.000") BigDecimal weightKg) { }
