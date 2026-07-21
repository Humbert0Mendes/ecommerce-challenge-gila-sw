package com.productorder.core.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class ProductFilterDomainTest {

    @Test
    void shouldIdentifyEmptyFilter() {
        assertThat(new ProductFilterDomain(null, null, null, null, null, null, null).isEmpty()).isTrue();
        assertThat(new ProductFilterDomain("Mouse", null, null, null, null, null, null).isEmpty()).isFalse();
    }

    @Test
    void shouldRejectPriceRangeWhenMinimumIsGreaterThanMaximum() {
        assertThatThrownBy(() -> new ProductFilterDomain(null, null, null, new BigDecimal("100.00"), new BigDecimal("10.00"), null, null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Minimum price cannot be greater than maximum price");
    }

    @Test
    void shouldRejectWeightRangeWhenMinimumIsGreaterThanMaximum() {
        assertThatThrownBy(() -> new ProductFilterDomain(null, null, null, null, null, new BigDecimal("2.000"), new BigDecimal("1.000")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Minimum weight cannot be greater than maximum weight");
    }
}
