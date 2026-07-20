package com.productorder.product.api;

import java.util.List;

public record ImportResponse(int imported, int skipped, List<ImportError> errors) { }
