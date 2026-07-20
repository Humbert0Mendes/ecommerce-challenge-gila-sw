package com.productorder.order.api;

import com.productorder.order.application.OrderService;
import jakarta.validation.Valid;

import java.net.URI;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OrderResponse> create(@Valid @RequestBody OrderCreateRequest request) {
        OrderResponse response = service.create(request);
        return ResponseEntity.created(URI.create("/api/v1/orders/" + response.id())).body(response);
    }

    @GetMapping
    public Page<OrderResponse> list(@PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return service.list(pageable);
    }

    @GetMapping("/{id}")
    public OrderResponse get(@PathVariable Long id) {
        return service.get(id);
    }
}
