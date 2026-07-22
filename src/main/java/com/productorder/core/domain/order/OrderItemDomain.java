package com.productorder.core.domain.order;

import java.math.BigDecimal;

public record OrderItemDomain(Long productId, String productName, int quantity, BigDecimal unitPrice) {
    public OrderItemDomain(Long productId, int quantity, BigDecimal unitPrice) {
        this(productId, null, quantity, unitPrice);
    }

    public BigDecimal subtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
