package com.productorder.dataprovider.csv;

import com.productorder.core.domain.product.ProductImportRow;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

@Component
public class ProductCsvParser {
    private static final List<String> HEADERS = List.of("name", "sku", "description", "category", "price", "stock", "weight_kg");

    public List<ProductImportRow> parse(InputStream input) {
        try (CSVParser parser = CSVFormat.DEFAULT.builder().setHeader().setSkipHeaderRecord(true).setTrim(true).get().parse(new InputStreamReader(input, StandardCharsets.UTF_8))) {
            if (!parser.getHeaderMap().keySet().containsAll(HEADERS))
                throw new IllegalArgumentException("Cabeçalho CSV inválido");
            return parser.stream().filter(record -> !record.stream().allMatch(String::isBlank)).map(this::row).toList();
        } catch (IOException exception) {
            throw new IllegalArgumentException("Não foi possível ler o CSV");
        }
    }

    private ProductImportRow row(CSVRecord record) {
        return new ProductImportRow(record.getRecordNumber() + 1,
                record.get("name"),
                record.get("sku"),
                record.get("description"),
                record.get("category"),
                record.get("price"),
                record.get("stock"),
                record.get("weight_kg"));
    }
}
