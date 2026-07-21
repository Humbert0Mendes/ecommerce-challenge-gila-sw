package com.productorder.core.domain.product;

import java.util.List;

public record ProductImportResult(int imported, int skipped, List<ProductImportIssue> issues) { }
