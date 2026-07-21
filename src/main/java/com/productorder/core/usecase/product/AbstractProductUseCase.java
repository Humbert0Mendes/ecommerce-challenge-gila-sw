package com.productorder.core.usecase.product;

import com.productorder.core.domain.product.ProductDomain;
import com.productorder.core.gateway.ProductGateway;
import com.productorder.core.usecase.ContentValidator;

public abstract class AbstractProductUseCase {
    protected final ProductGateway products;
    protected final ContentValidator validator;

    protected AbstractProductUseCase(ProductGateway products, ContentValidator validator) {
        this.products = products;
        this.validator = validator;
    }

    protected void validateProductData(ProductDomain c) {
        validator.validate(c.getName());
        validator.validate(c.getSku());
        validator.validate(c.getDescription());
        validator.validate(c.getCategory());
    }
}
