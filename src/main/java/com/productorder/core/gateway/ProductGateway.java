package com.productorder.core.gateway;

import com.productorder.core.domain.product.ProductDomain;
import com.productorder.core.domain.product.ProductFilterDomain;

import java.util.Optional;
import java.util.List;

public interface ProductGateway {
    ProductDomain save(ProductDomain productDomain);

    Optional<ProductDomain> findActiveById(Long id);

    Optional<ProductDomain> findBySku(String sku);

    Optional<ProductDomain> findActiveByIdForUpdate(Long id);

    Optional<ProductDomain> findByIdForUpdate(Long id);

    PageResult<ProductDomain> findActive(ProductFilterDomain filter, PageQuery page);

    List<String> findActiveCategories();
}
