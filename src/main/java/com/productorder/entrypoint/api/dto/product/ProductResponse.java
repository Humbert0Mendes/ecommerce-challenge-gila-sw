package com.productorder.entrypoint.api.dto.product;

import com.productorder.core.domain.product.ProductDomain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductResponse(Long id, String name, String sku, String description, String category, BigDecimal price,
                              int stock, BigDecimal weightKg, LocalDateTime createdAt, LocalDateTime updatedAt) {
    public static ProductResponse from(ProductDomain p) {
        return new ProductResponse(p.getId(), p.getName(), p.getSku(), p.getDescription(), p.getCategory(),
                p.getPrice(), p.getStock(), p.getWeightKg(), p.getCreatedAt(), p.getUpdatedAt());
    }
}
