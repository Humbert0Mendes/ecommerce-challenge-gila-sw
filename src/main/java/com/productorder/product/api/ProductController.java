package com.productorder.product.api;

import com.productorder.product.application.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.productorder.product.application.ProductImportService;

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products")
@Validated
public class ProductController {
    private final ProductService service;
    private final ProductImportService importService;

    public ProductController(ProductService service, ProductImportService importService) {
        this.service = service;
        this.importService = importService;
    }

    @PostMapping
    @Operation(summary = "Cria um produto")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        ProductResponse response = service.create(request);
        return ResponseEntity.created(URI.create("/api/v1/products/" + response.id())).body(response);
    }

    @GetMapping
    public Page<ProductResponse> list(@PageableDefault(size = 20) Pageable pageable) {
        return service.list(pageable);
    }

    @GetMapping("/search")
    public Page<ProductResponse> search(@RequestParam @Size(min = 1, max = 100) String q, @PageableDefault(size = 20) Pageable pageable) {
        return service.search(q, pageable);
    }

    @PostMapping(value = "/import", consumes = "multipart/form-data")
    public ImportResponse importCsv(@RequestPart("file") MultipartFile file) {
        return importService.importFile(file);
    }

    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return service.update(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
