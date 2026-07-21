package com.productorder.entrypoint.api.facade;

import com.productorder.core.domain.order.OrderDomain;
import com.productorder.core.domain.order.OrderIdempotencyDomain;
import com.productorder.core.domain.order.OrderItemDomain;
import com.productorder.core.gateway.PageQuery;
import com.productorder.core.gateway.PageResult;
import com.productorder.core.usecase.order.OrderUseCase;
import com.productorder.entrypoint.api.dto.PageResponse;
import com.productorder.entrypoint.api.dto.order.OrderCreateRequest;
import com.productorder.entrypoint.api.dto.order.OrderResponse;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class OrderFacade {
    private final OrderUseCase useCase;

    public OrderFacade(OrderUseCase useCase) {
        this.useCase = useCase;
    }

    public OrderResponse create(OrderCreateRequest request, String idempotencyKey, String subject) {
        var draft = OrderDomain.created();
        request.items().forEach(item -> draft.addItem(new OrderItemDomain(item.productId(), item.quantity(), null)));
        return OrderResponse.from(useCase.create(draft, new OrderIdempotencyDomain(subject, idempotencyKey)));
    }

    public OrderResponse get(Long id) {
        return OrderResponse.from(useCase.get(id));
    }

    public PageResponse<OrderResponse> list(Pageable pageable) {
        PageResult<OrderDomain> page = useCase.list(new PageQuery(pageable.getPageNumber(), Math.min(pageable.getPageSize(), 100), "createdAt", "DESC"));
        return new PageResponse<>(page.content().stream().map(OrderResponse::from).toList(), page.page(), page.size(), page.totalElements(), page.totalPages());
    }
}
