package com.productorder.product.application;

import com.productorder.product.api.ProductRequest;
import com.productorder.product.api.ProductResponse;
import com.productorder.product.domain.Product;
import com.productorder.product.persistence.ProductRepository;
import com.productorder.shared.error.NotFoundException;
import com.productorder.shared.validation.ContentValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {
    private final ProductRepository repository;
    private final ContentValidator contentValidator;
    public ProductService(ProductRepository repository, ContentValidator contentValidator) { this.repository = repository; this.contentValidator = contentValidator; }

    @Transactional
    public ProductResponse create(ProductRequest request) {
        validate(request);
        return ProductResponse.from(repository.save(new Product(request.name(), request.sku(), request.description(), request.category(), request.price(), request.stock(), request.weightKg())));
    }
    @Transactional(readOnly = true)
    public ProductResponse get(Long id) { return ProductResponse.from(active(id)); }
    @Transactional(readOnly = true)
    public Page<ProductResponse> list(Pageable pageable) { return repository.findAllByActiveTrue(pageable).map(ProductResponse::from); }
    @Transactional(readOnly = true)
    public Page<ProductResponse> search(String query, Pageable pageable) { return repository.searchActive(query, pageable).map(ProductResponse::from); }
    @Transactional
    public ProductResponse update(Long id, ProductRequest request) { validate(request); Product product = active(id); product.update(request.name(), request.sku(), request.description(), request.category(), request.price(), request.stock(), request.weightKg()); return ProductResponse.from(product); }
    @Transactional
    public void delete(Long id) { active(id).deactivate(); }
    public Product active(Long id) { return repository.findByIdAndActiveTrue(id).orElseThrow(() -> new NotFoundException("Produto")); }
    private void validate(ProductRequest r) { contentValidator.validate(r.name()); contentValidator.validate(r.sku()); contentValidator.validate(r.description()); contentValidator.validate(r.category()); }
}
