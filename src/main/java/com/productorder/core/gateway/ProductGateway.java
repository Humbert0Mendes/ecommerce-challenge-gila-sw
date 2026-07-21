package com.productorder.core.gateway;

import com.productorder.core.domain.product.ProductDomain;

import java.util.Optional;

public interface ProductGateway {
    ProductDomain save(ProductDomain productDomain);

    Optional<ProductDomain> findActiveById(Long id);

    Optional<ProductDomain> findBySku(String sku);

    Optional<ProductDomain> findActiveByIdForUpdate(Long id);

    PageResult<ProductDomain> findActive(PageQuery page);

    PageResult<ProductDomain> searchActive(String query, PageQuery page);
}
