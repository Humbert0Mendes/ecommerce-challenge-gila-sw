package com.productorder.entrypoint.api.dto.product;

import java.util.List;

public record ImportResponse(int imported, int skipped, List<ImportError> errors) {
}
