package com.productorder.entrypoint.api.controller;

import com.productorder.entrypoint.api.dto.order.OrderCreateRequest;
import com.productorder.entrypoint.api.dto.order.OrderResponse;
import com.productorder.entrypoint.api.dto.PageResponse;
import com.productorder.entrypoint.api.facade.OrderFacade;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import java.net.URI;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderFacade facade;

    public OrderController(OrderFacade facade) {
        this.facade = facade;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@RequestHeader("Idempotency-Key") @NotBlank String idempotencyKey, Authentication authentication, @Valid @RequestBody OrderCreateRequest request) {
        var response = facade.create(request, idempotencyKey, authentication.getName());
        return ResponseEntity.created(URI.create("/api/v1/orders/" + response.id())).body(response);
    }

    @GetMapping
    public PageResponse<OrderResponse> list(@PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return facade.list(pageable);
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable Long id) {
        return facade.get(id);
    }
}
