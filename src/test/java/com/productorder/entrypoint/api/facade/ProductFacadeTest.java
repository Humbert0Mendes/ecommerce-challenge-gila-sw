package com.productorder.entrypoint.api.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.productorder.core.domain.product.ProductDomain;
import com.productorder.core.domain.product.ProductFilterDomain;
import com.productorder.core.gateway.PageQuery;
import com.productorder.core.gateway.PageResult;
import com.productorder.core.usecase.product.ProductUseCase;
import com.productorder.entrypoint.api.dto.PageResponse;
import com.productorder.entrypoint.api.dto.product.ProductRequest;
import com.productorder.entrypoint.api.dto.product.ProductResponse;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class ProductFacadeTest {

    @Mock
    private ProductUseCase useCase;

    @Captor
    private ArgumentCaptor<ProductDomain> productCaptor;

    @Captor
    private ArgumentCaptor<ProductFilterDomain> filterCaptor;

    @Captor
    private ArgumentCaptor<PageQuery> pageCaptor;

    private ProductFacade facade;

    @BeforeEach
    void setUp() {
        facade = new ProductFacade(useCase);
    }

    @Test
    void shouldMapProductRequestToDomainWhenCreatingProduct() {
        ProductRequest request = request();
        ProductDomain saved = product(1L, "Mouse", "MOUSE-001");
        when(useCase.create(org.mockito.ArgumentMatchers.any(ProductDomain.class))).thenReturn(saved);

        ProductResponse response = facade.create(request);

        verify(useCase).create(productCaptor.capture());
        ProductDomain captured = productCaptor.getValue();
        assertThat(captured.getName()).isEqualTo("Mouse");
        assertThat(captured.getSku()).isEqualTo("MOUSE-001");
        assertThat(response.id()).isEqualTo(1L);
    }

    @Test
    void shouldNormalizeFiltersAndMapProductPage() {
        ProductDomain product = product(1L, "Mouse", "MOUSE-001");
        PageResult<ProductDomain> result = new PageResult<>(List.of(product), 0, 100, 1, 1);
        when(useCase.list(org.mockito.ArgumentMatchers.any(ProductFilterDomain.class), org.mockito.ArgumentMatchers.any(PageQuery.class))).thenReturn(result);

        PageResponse<ProductResponse> response = facade.list(" Mouse ", " ", " Perifericos ", new BigDecimal("10.00"), new BigDecimal("100.00"), null, null, PageRequest.of(0, 200));

        verify(useCase).list(filterCaptor.capture(), pageCaptor.capture());
        assertThat(filterCaptor.getValue()).isEqualTo(new ProductFilterDomain("Mouse", null, "Perifericos", new BigDecimal("10.00"), new BigDecimal("100.00"), null, null));
        assertThat(pageCaptor.getValue()).isEqualTo(new PageQuery(0, 100, "name", "ASC"));
        assertThat(response.content()).extracting(ProductResponse::sku).containsExactly("MOUSE-001");
    }

    private ProductRequest request() {
        return new ProductRequest("Mouse", "MOUSE-001", "Mouse sem fio", "Perifericos", new BigDecimal("99.90"), 10, new BigDecimal("0.120"));
    }

    private ProductDomain product(Long id, String name, String sku) {
        return new ProductDomain(id, name, sku, "Mouse sem fio", "Perifericos", new BigDecimal("99.90"), 10, new BigDecimal("0.120"), true, null, null);
    }
}
