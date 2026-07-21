package com.productorder.entrypoint.api.facade;

import com.productorder.core.domain.product.ProductImportResult;
import com.productorder.core.usecase.product.ProductUseCase;
import com.productorder.dataprovider.csv.ProductCsvParser;
import com.productorder.entrypoint.api.dto.product.ImportError;
import com.productorder.entrypoint.api.dto.product.ImportResponse;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ProductImportFacade {
    private final ProductCsvParser parser;
    private final ProductUseCase useCase;

    public ProductImportFacade(ProductCsvParser parser, ProductUseCase useCase) {
        this.parser = parser;
        this.useCase = useCase;
    }

    public ImportResponse importFile(MultipartFile file) {
        if (file.isEmpty()) throw new IllegalArgumentException("CSV file is empty");
        try {
            var csvParsed = parser.parse(file.getInputStream());
            var importedProducts = useCase.importProducts(csvParsed);
            return response(importedProducts);
        } catch (IOException exception) {
            throw new IllegalArgumentException("Was not able to read CSV file", exception);
        }
    }

    private ImportResponse response(ProductImportResult result) {
        return new ImportResponse(result.imported(), result.skipped(), result.issues().stream().map(issue -> new ImportError(issue.line(), issue.field(), issue.message())).toList());
    }
}
