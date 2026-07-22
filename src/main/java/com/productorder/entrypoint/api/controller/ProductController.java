package com.productorder.entrypoint.api.controller;

import com.productorder.entrypoint.api.dto.product.ImportResponse;
import com.productorder.entrypoint.api.dto.PageResponse;
import com.productorder.entrypoint.api.dto.product.ProductRequest;
import com.productorder.entrypoint.api.dto.product.ProductResponse;
import com.productorder.entrypoint.api.facade.ProductFacade;
import com.productorder.entrypoint.api.facade.ProductImportFacade;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.net.URI;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products")
@Validated
public class ProductController {
    private final ProductFacade facade;
    private final ProductImportFacade importFacade;

    public ProductController(ProductFacade facade, ProductImportFacade importFacade) {
        this.facade = facade;
        this.importFacade = importFacade;
    }

    @PostMapping
    @Operation(summary = "Cria um produto")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = facade.create(request);
        return ResponseEntity.created(URI.create("/api/v1/products/" + response.id())).body(response);
    }

    @GetMapping
    @Operation(summary = "Lista produtos aplicando filtros opcionais")
    public PageResponse<ProductResponse> list(
            @RequestParam(required = false) @Size(max = 255) String name,
            @RequestParam(required = false) @Size(max = 20) String sku,
            @RequestParam(required = false) @Size(max = 100) String category,
            @RequestParam(required = false) @DecimalMin("0.00") BigDecimal minPrice,
            @RequestParam(required = false) @DecimalMin("0.00") BigDecimal maxPrice,
            @RequestParam(required = false) @DecimalMin("0.000") BigDecimal minWeight,
            @RequestParam(required = false) @DecimalMin("0.000") BigDecimal maxWeight,
            @PageableDefault(size = 20) Pageable pageable) {
        return facade.list(name, sku, category, minPrice, maxPrice, minWeight, maxWeight, pageable);
    }

    @GetMapping("/categories")
    @Operation(summary = "Lista as categorias de produtos ativas")
    public java.util.List<String> categories() {
        return facade.categories();
    }

    @PostMapping(value = "/import", consumes = "multipart/form-data")
    public ImportResponse importCsv(@RequestPart("file") MultipartFile file) {
        return importFacade.importFile(file);
    }

    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return facade.get(id);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return facade.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        facade.delete(id);
        return ResponseEntity.noContent().build();
    }

}
