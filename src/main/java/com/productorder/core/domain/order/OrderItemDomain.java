package com.productorder.core.domain.order;
import java.math.BigDecimal;
public record OrderItemDomain(Long productId, int quantity, BigDecimal unitPrice) {
    public BigDecimal subtotal() { return unitPrice.multiply(BigDecimal.valueOf(quantity)); }
}
