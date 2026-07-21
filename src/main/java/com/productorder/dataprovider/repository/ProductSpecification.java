package com.productorder.dataprovider.repository;

import com.productorder.core.domain.product.ProductFilterDomain;
import com.productorder.dataprovider.entity.ProductEntity;

import java.util.Locale;

import org.springframework.data.jpa.domain.Specification;

public final class ProductSpecification {

    private ProductSpecification() {
    }

    public static Specification<ProductEntity> from(ProductFilterDomain filter) {
        Specification<ProductEntity> specification = active();

        if (filter.name() != null) {
            specification = specification.and(nameContains(filter.name()));
        }
        if (filter.sku() != null) {
            specification = specification.and(skuEquals(filter.sku()));
        }
        if (filter.category() != null) {
            specification = specification.and(categoryEquals(filter.category()));
        }
        if (filter.minPrice() != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("price"), filter.minPrice()));
        }
        if (filter.maxPrice() != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("price"), filter.maxPrice()));
        }
        if (filter.minWeight() != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("weightKg"), filter.minWeight()));
        }
        if (filter.maxWeight() != null) {
            specification = specification.and((root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("weightKg"), filter.maxWeight()));
        }
        return specification;
    }

    private static Specification<ProductEntity> active() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.isTrue(root.get("active"));
    }

    private static Specification<ProductEntity> nameContains(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase(Locale.ROOT) + "%");
    }

    private static Specification<ProductEntity> skuEquals(String sku) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("sku"), sku);
    }

    private static Specification<ProductEntity> categoryEquals(String category) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(criteriaBuilder.lower(root.get("category")), category.toLowerCase(Locale.ROOT));
    }
}
