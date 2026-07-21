package com.productorder.entrypoint.api.facade;

import com.productorder.core.domain.product.ProductDomain;
import com.productorder.core.domain.product.ProductFilterDomain;
import com.productorder.core.gateway.PageQuery;
import com.productorder.core.gateway.PageResult;
import com.productorder.core.usecase.product.ProductUseCase;
import com.productorder.entrypoint.api.dto.PageResponse;
import com.productorder.entrypoint.api.dto.product.ProductRequest;
import com.productorder.entrypoint.api.dto.product.ProductResponse;

import java.math.BigDecimal;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class ProductFacade {
    private final ProductUseCase useCase;
    public ProductFacade(ProductUseCase useCase) {
        this.useCase = useCase;
    }

    public ProductResponse create(ProductRequest request) {
        return ProductResponse.from(useCase.create(toDomain(request)));
    }

    public ProductResponse get(Long id) {
        return ProductResponse.from(useCase.get(id));
    }

    public ProductResponse update(Long id, ProductRequest request) {
        return ProductResponse.from(useCase.update(id, toDomain(request)));
    }

    public void delete(Long id) {
        useCase.delete(id);
    }

    public PageResponse<ProductResponse> list(String name, String sku, String category, BigDecimal minPrice, BigDecimal maxPrice,
                                      BigDecimal minWeight, BigDecimal maxWeight, Pageable pageable) {
        var filter = buildProductFilterDomain(name, sku, category, minPrice, maxPrice, minWeight, maxWeight);
        return responsePage(useCase.list(filter, page(pageable)));
    }

    private @NonNull ProductFilterDomain buildProductFilterDomain(String name, String sku, String category,
                                                                  BigDecimal minPrice, BigDecimal maxPrice, BigDecimal minWeight, BigDecimal maxWeight) {
        return new ProductFilterDomain(normalize(name), normalize(sku), normalize(category), minPrice, maxPrice, minWeight, maxWeight);
    }

    private ProductDomain toDomain(ProductRequest request) {
        return ProductDomain.create(request.name(), request.sku(), request.description(), request.category(),
                request.price(), request.stock(), request.weightKg());
    }

    private String  normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private PageQuery page(Pageable pageable) {
        return new PageQuery(pageable.getPageNumber(), Math.min(pageable.getPageSize(), 100), pageable.getSort().isSorted() ? pageable.getSort().iterator().next().getProperty() : "name", pageable.getSort().isSorted() ? pageable.getSort().iterator().next().getDirection().name() : "ASC");
    }

    private PageResponse<ProductResponse> responsePage(PageResult<ProductDomain> page) {
        return new PageResponse<>(page.content().stream().map(ProductResponse::from).toList(), page.page(), page.size(), page.totalElements(), page.totalPages());
    }
}
