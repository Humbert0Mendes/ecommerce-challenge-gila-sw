package com.productorder.core.usecase.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.productorder.core.domain.product.ProductDomain;
import com.productorder.core.domain.product.ProductImportRow;
import com.productorder.core.gateway.ProductGateway;

import java.math.BigDecimal;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductImportLineProcessorTest {

    @Mock
    private ProductGateway products;

    @Captor
    private ArgumentCaptor<ProductDomain> productCaptor;

    private ProductImportLineProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new ProductImportLineProcessor(products);
    }

    @Test
    void shouldNormalizeFreePriceAndNegativeStockWhenImportingProduct() {
        ProductImportRow row = new ProductImportRow(2, "Mouse", "MOUSE-001", "Mouse sem fio", "Perifericos", "free", "-4", "0.120");
        when(products.findBySku("MOUSE-001")).thenReturn(Optional.empty());

        processor.importLine(row);

        verify(products).save(productCaptor.capture());
        ProductDomain product = productCaptor.getValue();
        assertThat(product.getPrice()).isEqualByComparingTo(BigDecimal.ZERO.setScale(2));
        assertThat(product.getStock()).isZero();
        assertThat(product.getWeightKg()).isEqualByComparingTo("0.120");
    }

    @Test
    void shouldReactivateAndUpdateExistingProductWhenSkuAlreadyExists() {
        ProductDomain existing = new ProductDomain(1L, "Old mouse", "MOUSE-001", "Old", "Perifericos", new BigDecimal("10.00"), 1, new BigDecimal("0.100"), false, null, null);
        ProductImportRow row = new ProductImportRow(2, "Mouse", "MOUSE-001", "Mouse sem fio", "Perifericos", "$29.99", "3", "0.120");
        when(products.findBySku("MOUSE-001")).thenReturn(Optional.of(existing));

        processor.importLine(row);

        verify(products).save(existing);
        assertThat(existing.isActive()).isTrue();
        assertThat(existing.getName()).isEqualTo("Mouse");
        assertThat(existing.getPrice()).isEqualByComparingTo("29.99");
    }

    @Test
    void shouldRejectInvalidPriceWithoutPersistingProduct() {
        ProductImportRow row = new ProductImportRow(2, "Mouse", "MOUSE-001", "Mouse sem fio", "Perifericos", "invalid", "3", "0.120");

        assertThatThrownBy(() -> processor.importLine(row))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("price invalid");

        verify(products, org.mockito.Mockito.never()).save(any());
    }

    @Test
    void shouldAcceptZeroPriceZeroWeightAndPositiveStock() {
        ProductImportRow row = new ProductImportRow(2, "Mouse", "MOUSE-001", "Mouse sem fio", "Perifericos", "0.00", "3", "0.000");
        when(products.findBySku("MOUSE-001")).thenReturn(Optional.empty());

        processor.importLine(row);

        verify(products).save(productCaptor.capture());
        ProductDomain product = productCaptor.getValue();
        assertThat(product.getPrice()).isEqualByComparingTo("0.00");
        assertThat(product.getWeightKg()).isEqualByComparingTo("0.000");
        assertThat(product.getStock()).isEqualTo(3);
    }
}
