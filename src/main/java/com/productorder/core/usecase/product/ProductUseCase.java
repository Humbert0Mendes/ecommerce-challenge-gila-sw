package com.productorder.core.usecase.product;

import com.productorder.core.domain.product.ProductDomain;
import com.productorder.core.domain.product.ProductImportIssue;
import com.productorder.core.domain.product.ProductImportResult;
import com.productorder.core.domain.product.ProductImportRow;
import com.productorder.core.exception.NotFoundException;
import com.productorder.core.gateway.PageQuery;
import com.productorder.core.gateway.PageResult;
import com.productorder.core.gateway.ProductGateway;
import com.productorder.core.usecase.ContentValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductUseCase extends AbstractProductUseCase {
    public ProductUseCase(ProductGateway products) {
        super(products, new ContentValidator());
    }

    @Transactional
    public ProductDomain create(ProductDomain c) {
        validateProductData(c);
        return products.save(c);
    }

    @Transactional(readOnly = true)
    public ProductDomain get(Long id) {
        return active(id);
    }

    @Transactional(readOnly = true)
    public PageResult<ProductDomain> list(PageQuery q) {
        return products.findActive(q);
    }

    @Transactional(readOnly = true)
    public PageResult<ProductDomain> search(String q, PageQuery page) {
        return products.searchActive(q, page);
    }

    @Transactional
    public ProductDomain update(Long id, ProductDomain c) {
        validateProductData(c);
        var product = active(id);
        product.update(c.getName(), c.getSku(), c.getDescription(), c.getCategory(), c.getPrice(), c.getStock(), c.getWeightKg());
        return products.save(product);
    }

    @Transactional
    public void delete(Long id) {
        var product = active(id);
        product.deactivate();
        products.save(product);
    }

    public ProductDomain active(Long id) {
        return products.findActiveById(id).orElseThrow(() -> new NotFoundException("Produto"));
    }

    @Transactional
    public ProductImportResult importProducts(List<ProductImportRow> rows) {
        List<ProductImportIssue> issues = new ArrayList<>();
        int imported = 0;
        for (ProductImportRow row : rows) {
            try {
                ProductDomain product = ProductDomain.create(required(row.name(), "name"), required(row.sku(), "sku"), required(row.description(), "description"), required(row.category(), "category"), price(row.price()), stock(row.stock()), decimal(row.weightKg(), "weight_kg"));
                if (product.getWeightKg().signum() < 0) {
                    throw new IllegalArgumentException("weight_kg inválido");
                }
                validateProductData(product);
                createOrUpdateProductBySku(product);
                imported++;
            } catch (RuntimeException exception) {
                issues.add(new ProductImportIssue(row.line(), "row", exception.getMessage()));
            }
        }
        return new ProductImportResult(imported, issues.size(), List.copyOf(issues));
    }

    private void createOrUpdateProductBySku(ProductDomain product) {
        ProductDomain existing = products.findBySku(product.getSku()).orElse(null);
        if (existing == null) {
            products.save(product);
        } else {
            existing.update(product.getName(), product.getSku(), product.getDescription(), product.getCategory(), product.getPrice(), product.getStock(), product.getWeightKg());
            existing.reactivate();
            products.save(existing);
        }
    }

    private String required(String value, String field) {
        if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " é obrigatório");
        return value;
    }

    private BigDecimal price(String value) {
        if ("free".equalsIgnoreCase(value.trim())) return BigDecimal.ZERO.setScale(2);
        BigDecimal result = decimal(value.trim().replace("$", ""), "price");
        if (result.signum() < 0) throw new IllegalArgumentException("price inválido");
        return result;
    }

    private int stock(String value) {
        try {
            return Math.max(0, Integer.parseInt(value.trim()));
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException("stock inválido");
        }
    }

    private BigDecimal decimal(String value, String field) {
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException exception) {
            throw new IllegalArgumentException(field + " inválido");
        }
    }
}
