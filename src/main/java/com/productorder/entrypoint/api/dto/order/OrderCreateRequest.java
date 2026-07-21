package com.productorder.entrypoint.api.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record OrderCreateRequest(@NotEmpty List<@Valid OrderItemRequest> items) {
}
