package com.productorder.product.application;

import com.productorder.product.api.ImportError;
import com.productorder.product.api.ImportResponse;
import com.productorder.product.domain.Product;
import com.productorder.product.persistence.ProductRepository;
import com.productorder.shared.validation.ContentValidator;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ProductImportService {
    private static final List<String> HEADERS = List.of("name", "sku", "description", "category", "price", "stock", "weight_kg");
    private final ProductRepository repository;
    private final ContentValidator validator;
    public ProductImportService(ProductRepository repository, ContentValidator validator) { this.repository = repository; this.validator = validator; }

    @Transactional
    public ImportResponse importFile(MultipartFile file) {
        if (file.isEmpty()) throw new IllegalArgumentException("Arquivo CSV vazio");
        List<ImportError> errors = new ArrayList<>(); int imported = 0;
        try (CSVParser parser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setTrim(true).build().parse(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            if (!parser.getHeaderMap().keySet().containsAll(HEADERS)) throw new IllegalArgumentException("Cabeçalho CSV inválido");
            for (CSVRecord record : parser) {
                if (record.stream().allMatch(String::isBlank)) continue;
                try { upsert(record); imported++; }
                catch (IllegalArgumentException ex) { errors.add(new ImportError(record.getRecordNumber() + 1, "row", ex.getMessage())); }
            }
        } catch (IOException ex) { throw new IllegalArgumentException("Não foi possível ler o CSV"); }
        return new ImportResponse(imported, errors.size(), List.copyOf(errors));
    }

    private void upsert(CSVRecord r) {
        String name = required(r, "name"), sku = required(r, "sku"), description = required(r, "description"), category = required(r, "category");
        validator.validate(name); validator.validate(sku); validator.validate(description); validator.validate(category);
        BigDecimal price = price(r.get("price"));
        int stock = stock(r.get("stock"));
        BigDecimal weight = decimal(r.get("weight_kg"), "weight_kg");
        if (weight.signum() < 0) throw new IllegalArgumentException("weight_kg inválido");
        Product product = repository.findBySku(sku).orElse(null);
        if (product == null) repository.save(new Product(name, sku, description, category, price, stock, weight));
        else { product.update(name, sku, description, category, price, stock, weight); product.reactivate(); }
    }
    private String required(CSVRecord r, String field) { String value = r.get(field); if (value == null || value.isBlank()) throw new IllegalArgumentException(field + " é obrigatório"); return value; }
    private BigDecimal price(String value) { if ("free".equalsIgnoreCase(value.trim())) return BigDecimal.ZERO.setScale(2); String normalized = value.trim().replace("$", ""); BigDecimal result = decimal(normalized, "price"); if (result.signum() < 0) throw new IllegalArgumentException("price inválido"); return result; }
    private int stock(String value) { try { return Math.max(0, Integer.parseInt(value.trim())); } catch (NumberFormatException ex) { throw new IllegalArgumentException("stock inválido"); } }
    private BigDecimal decimal(String value, String field) { try { return new BigDecimal(value.trim()); } catch (NumberFormatException ex) { throw new IllegalArgumentException(field + " inválido"); } }
}
