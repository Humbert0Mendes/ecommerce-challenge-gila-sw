package com.productorder.core.usecase.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.productorder.core.domain.product.ProductDomain;
import com.productorder.core.domain.product.ProductFilterDomain;
import com.productorder.core.domain.product.ProductImportResult;
import com.productorder.core.domain.product.ProductImportRow;
import com.productorder.core.exception.BusinessRuleException;
import com.productorder.core.exception.NotFoundException;
import com.productorder.core.gateway.PageQuery;
import com.productorder.core.gateway.PageResult;
import com.productorder.core.gateway.ProductGateway;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductUseCaseTest {

    @Mock
    private ProductGateway products;

    @Mock
    private ProductImportLineProcessor importLineProcessor;

    private ProductUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ProductUseCase(products, importLineProcessor);
    }

    @Test
    void shouldPersistValidProduct() {
        ProductDomain product = product("Mouse", "MOUSE-001");
        when(products.save(product)).thenReturn(product);

        ProductDomain result = useCase.create(product);

        assertThat(result).isSameAs(product);
        verify(products).save(product);
    }

    @Test
    void shouldRejectProductWithDangerousText() {
        ProductDomain product = product("<script>alert(1)</script>", "MOUSE-001");

        assertThatThrownBy(() -> useCase.create(product))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Text contains dangerous characters");
    }

    @Test
    void shouldRejectProductWithDangerousSku() {
        ProductDomain product = product("Mouse", "MOUSE-001; drop table products");

        assertThatThrownBy(() -> useCase.create(product))
                .isInstanceOf(BusinessRuleException.class)
                .hasMessage("Text contains dangerous characters");
    }

    @Test
    void shouldReturnActiveProductById() {
        ProductDomain product = product("Mouse", "MOUSE-001");
        when(products.findActiveById(1L)).thenReturn(Optional.of(product));

        assertThat(useCase.get(1L)).isSameAs(product);
    }

    @Test
    void shouldRejectLookupForInactiveOrMissingProduct() {
        when(products.findActiveById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.get(1L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("Produto not found");
    }

    @Test
    void shouldReturnFilteredProductPage() {
        ProductDomain product = product("Mouse", "MOUSE-001");
        ProductFilterDomain filter = new ProductFilterDomain("Mouse", null, null, null, null, null, null);
        PageQuery page = new PageQuery(0, 20, "name", "ASC");
        PageResult<ProductDomain> expected = new PageResult<>(List.of(product), 0, 20, 1, 1);
        when(products.findActive(filter, page)).thenReturn(expected);

        assertThat(useCase.list(filter, page)).isSameAs(expected);
    }

    @Test
    void shouldUpdateActiveProduct() {
        ProductDomain existing = product("Mouse", "MOUSE-001");
        ProductDomain update = product("Mouse atualizado", "MOUSE-001");
        when(products.findActiveById(1L)).thenReturn(Optional.of(existing));
        when(products.save(existing)).thenReturn(existing);

        ProductDomain result = useCase.update(1L, update);

        assertThat(result.getName()).isEqualTo("Mouse atualizado");
        verify(products).save(existing);
    }

    @Test
    void shouldDeactivateActiveProduct() {
        ProductDomain product = product("Mouse", "MOUSE-001");
        when(products.findActiveById(1L)).thenReturn(Optional.of(product));

        useCase.delete(1L);

        assertThat(product.isActive()).isFalse();
        verify(products).save(product);
    }

    @Test
    void shouldContinueBatchImportWhenOneLineFails() {
        ProductImportRow validRow = row(2, "MOUSE-001");
        ProductImportRow invalidRow = row(3, "MOUSE-002");
        doAnswer(invocation -> {
            ProductImportRow row = invocation.getArgument(0);
            if (row.line() == invalidRow.line()) {
                throw new IllegalArgumentException("invalid price");
            }
            return null;
        }).when(importLineProcessor).importLine(any(ProductImportRow.class));

        ProductImportResult result = useCase.importProducts(List.of(validRow, invalidRow));

        assertThat(result.imported()).isEqualTo(1);
        assertThat(result.skipped()).isEqualTo(1);
        assertThat(result.issues()).singleElement()
                .satisfies(issue -> {
                    assertThat(issue.line()).isEqualTo(3);
                    assertThat(issue.field()).isEqualTo("row");
                    assertThat(issue.message()).isEqualTo("invalid price");
                });
        verify(importLineProcessor).importLine(validRow);
        verify(importLineProcessor).importLine(invalidRow);
    }

    private ProductDomain product(String name, String sku) {
        return ProductDomain.create(name, sku, "Mouse sem fio", "Perifericos", new BigDecimal("99.90"), 10, new BigDecimal("0.120"));
    }

    private ProductImportRow row(long line, String sku) {
        return new ProductImportRow(line, "Mouse", sku, "Mouse sem fio", "Perifericos", "99.90", "10", "0.120");
    }
}
