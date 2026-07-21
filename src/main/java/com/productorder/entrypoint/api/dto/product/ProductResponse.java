package com.productorder.entrypoint.api.dto.product;

import com.productorder.core.domain.product.ProductDomain;

import java.math.BigDecimal;

public record ProductResponse(Long id, String name, String sku, String description, String category, BigDecimal price,
                              int stock, BigDecimal weightKg) {

    public static ProductResponse from(ProductDomain productDomain) {
        return new ProductResponse(productDomain.getId(),
                productDomain.getName(),
                productDomain.getSku(),
                productDomain.getDescription(),
                productDomain.getCategory(),
                productDomain.getPrice(),
                productDomain.getStock(),
                productDomain.getWeightKg());
    }
}
