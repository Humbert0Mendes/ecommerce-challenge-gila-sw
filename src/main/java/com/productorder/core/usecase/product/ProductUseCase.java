package com.productorder.core.usecase.product;

import com.productorder.core.domain.product.ProductDomain;
import com.productorder.core.domain.product.ProductFilterDomain;
import com.productorder.core.domain.product.ProductImportIssue;
import com.productorder.core.domain.product.ProductImportResult;
import com.productorder.core.domain.product.ProductImportRow;
import com.productorder.core.exception.NotFoundException;
import com.productorder.core.gateway.PageQuery;
import com.productorder.core.gateway.PageResult;
import com.productorder.core.gateway.ProductGateway;
import com.productorder.core.usecase.ContentValidator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductUseCase extends AbstractProductUseCase {
    private final ProductImportLineProcessor importLineUseCase;

    public ProductUseCase(ProductGateway products, ProductImportLineProcessor importLineUseCase) {
        super(products, new ContentValidator());
        this.importLineUseCase = importLineUseCase;
    }

    @Transactional
    public ProductDomain create(ProductDomain product) {
        validateProductData(product);
        return products.save(product);
    }

    @Transactional(readOnly = true)
    public ProductDomain get(Long id) {
        return active(id);
    }

    @Transactional(readOnly = true)
    public PageResult<ProductDomain> list(ProductFilterDomain filter, PageQuery page) {
        return products.findActive(filter, page);
    }

    @Transactional
    public ProductDomain update(Long id, ProductDomain product) {
        validateProductData(product);
        ProductDomain existing = active(id);
        existing.update(product.getName(), product.getSku(), product.getDescription(), product.getCategory(), product.getPrice(), product.getStock(), product.getWeightKg());
        return products.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        ProductDomain product = active(id);
        product.deactivate();
        products.save(product);
    }

    public ProductDomain active(Long id) {
        return products.findActiveById(id).orElseThrow(() -> new NotFoundException("Produto"));
    }

    public ProductImportResult importProducts(List<ProductImportRow> rows) {
        List<ProductImportIssue> issues = new ArrayList<>();
        int imported = 0;
        for (ProductImportRow row : rows) {
            try {
                importLineUseCase.importLine(row);
                imported++;
            } catch (RuntimeException exception) {
                issues.add(new ProductImportIssue(row.line(), "row", exception.getMessage()));
            }
        }
        return new ProductImportResult(imported, issues.size(), List.copyOf(issues));
    }
}
