package com.productorder.core.usecase.product;

import com.productorder.core.domain.product.ProductDomain;
import com.productorder.core.domain.product.ProductImportRow;
import com.productorder.core.gateway.ProductGateway;
import com.productorder.core.usecase.ContentValidator;

import java.math.BigDecimal;

import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductImportLineProcessor {
    private final ProductGateway products;
    private final ContentValidator validator = new ContentValidator();

    public ProductImportLineProcessor(ProductGateway products) {
        this.products = products;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void importLine(ProductImportRow row) {
        ProductDomain product = buildNewProduct(row);
        if (product.getWeightKg().signum() < 0) throw new IllegalArgumentException("invalid weight");
        validator.validate(product.getName());
        validator.validate(product.getSku());
        validator.validate(product.getDescription());
        validator.validate(product.getCategory());
        createOrUpdateProductBySku(product);
    }

    private @NonNull ProductDomain buildNewProduct(ProductImportRow row) {
        return ProductDomain.create(required(row.name(), "name"),
                required(row.sku(), "sku"),
                required(row.description(), "description"),
                required(row.category(), "category"),
                price(row.price()),
                stock(row.stock()),
                decimal(row.weightKg(), "weight_kg"));
    }

    private void createOrUpdateProductBySku(ProductDomain product) {
        ProductDomain existing = products.findBySku(product.getSku()).orElse(null);
        if (existing == null) products.save(product);
        else {
            existing.update(product.getName(), product.getSku(), product.getDescription(), product.getCategory(), product.getPrice(), product.getStock(), product.getWeightKg());
            existing.reactivate();
            products.save(existing);
        }
    }

    private String required(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " is required");
        return value;
    }

    private BigDecimal price(String value) {
        if ("free".equalsIgnoreCase(value.trim())) return BigDecimal.ZERO.setScale(2);
        BigDecimal result = decimal(value.trim().replace("$", ""), "price");
        if (result.signum() < 0) throw new IllegalArgumentException("invalid price");
        return result;
    }

    private int stock(String value) {
        try {
            return Math.max(0, Integer.parseInt(value.trim()));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("invalid stock value");
        }
    }

    private BigDecimal decimal(String value, String field) {
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(field + " invalid");
        }
    }
}
