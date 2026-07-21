package com.productorder.entrypoint.api.facade;

import com.productorder.core.domain.product.ProductDomain;
import com.productorder.core.gateway.PageQuery;
import com.productorder.core.gateway.PageResult;
import com.productorder.core.usecase.product.ProductUseCase;
import com.productorder.entrypoint.api.dto.product.ProductRequest;
import com.productorder.entrypoint.api.dto.product.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

    public Page<ProductResponse> list(Pageable pageable) {
        return responsePage(useCase.list(page(pageable)));
    }

    public Page<ProductResponse> search(String query, Pageable pageable) {
        return responsePage(useCase.search(query, page(pageable)));
    }

    private ProductDomain toDomain(ProductRequest request) {
        return ProductDomain.create(request.name(), request.sku(), request.description(), request.category(), request.price(), request.stock(), request.weightKg());
    }

    private PageQuery page(Pageable pageable) {
        return new PageQuery(pageable.getPageNumber(), Math.min(pageable.getPageSize(), 100), pageable.getSort().isSorted() ? pageable.getSort().iterator().next().getProperty() : "name", pageable.getSort().isSorted() ? pageable.getSort().iterator().next().getDirection().name() : "ASC");
    }

    private Page<ProductResponse> responsePage(PageResult<ProductDomain> page) {
        return new PageImpl<>(page.content().stream().map(ProductResponse::from).toList(), PageRequest.of(page.page(), page.size()), page.totalElements());
    }
}
