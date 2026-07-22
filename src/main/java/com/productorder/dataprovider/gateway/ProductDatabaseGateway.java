package com.productorder.dataprovider.gateway;

import com.productorder.core.domain.product.ProductDomain;
import com.productorder.core.domain.product.ProductFilterDomain;
import com.productorder.core.gateway.PageQuery;
import com.productorder.core.gateway.PageResult;
import com.productorder.core.gateway.ProductGateway;
import com.productorder.dataprovider.entity.ProductEntity;
import com.productorder.dataprovider.mapper.ProductEntityMapper;
import com.productorder.dataprovider.repository.ProductJpaRepository;
import com.productorder.dataprovider.repository.ProductSpecification;

import java.util.Optional;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

@Component
public class ProductDatabaseGateway implements ProductGateway {
    private final ProductJpaRepository repository;
    private final ProductEntityMapper mapper;

    public ProductDatabaseGateway(ProductJpaRepository repository, ProductEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    public ProductDomain save(ProductDomain product) {
        ProductEntity productEntity = product.getId() == null ? mapper.toEntity(product) : repository.findById(product.getId()).orElseGet(() -> mapper.toEntity(product));
        if (product.getId() != null) mapper.update(product, productEntity);
        return mapper.toDomain(repository.save(productEntity));
    }

    public Optional<ProductDomain> findActiveById(Long id) {
        return repository.findByIdAndActiveTrue(id).map(mapper::toDomain);
    }

    public Optional<ProductDomain> findBySku(String sku) {
        return repository.findBySku(sku).map(mapper::toDomain);
    }

    public Optional<ProductDomain> findActiveByIdForUpdate(Long id) {
        return repository.findActiveByIdForUpdate(id).map(mapper::toDomain);
    }

    public Optional<ProductDomain> findByIdForUpdate(Long id) {
        return repository.findByIdForUpdate(id).map(mapper::toDomain);
    }

    public PageResult<ProductDomain> findActive(ProductFilterDomain filter, PageQuery pageQuery) {
        Pageable pageable = page(pageQuery);
        if (filter.isEmpty()) {
            return page(repository.findAllByActiveTrue(pageable));
        }
        return page(repository.findAll(ProductSpecification.from(filter), pageable));
    }

    public List<String> findActiveCategories() {
        return repository.findDistinctActiveCategories();
    }

    private Pageable page(PageQuery pageQuery) {
        return PageRequest.of(pageQuery.page(), pageQuery.size(), Sort.by(Sort.Direction.fromOptionalString(pageQuery.direction()).orElse(Sort.Direction.ASC), pageQuery.sort()));
    }

    private PageResult<ProductDomain> page(Page<ProductEntity> page) {
        return new PageResult<>(page.getContent().stream().map(mapper::toDomain).toList(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }
}
