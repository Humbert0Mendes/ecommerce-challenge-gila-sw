package com.productorder.core.domain.product;

import java.math.BigDecimal;

public record ProductFilterDomain(
        String name,
        String sku,
        String category,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        BigDecimal minWeight,
        BigDecimal maxWeight) {

    public ProductFilterDomain {
        validateRange(minPrice, maxPrice, "price");
        validateRange(minWeight, maxWeight, "weight");
    }

    private static void validateRange(BigDecimal minimum, BigDecimal maximum, String field) {
        if (minimum != null && maximum != null && minimum.compareTo(maximum) > 0) {
            throw new IllegalArgumentException("Minimum " + field + " cannot be greater than maximum " + field);
        }
    }

    public boolean isEmpty() {
        return name == null
                && sku == null
                && category == null
                && minPrice == null
                && maxPrice == null
                && minWeight == null
                && maxWeight == null;
    }
}
