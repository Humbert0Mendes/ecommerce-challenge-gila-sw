package com.productorder.entrypoint.api.facade;

import com.productorder.core.domain.order.OrderDomain;
import com.productorder.core.domain.order.OrderIdempotencyDomain;
import com.productorder.core.domain.order.OrderItemDomain;
import com.productorder.core.gateway.PageQuery;
import com.productorder.core.gateway.PageResult;
import com.productorder.core.usecase.order.OrderUseCase;
import com.productorder.entrypoint.api.dto.order.OrderCreateRequest;
import com.productorder.entrypoint.api.dto.order.OrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class OrderFacade {
    private final OrderUseCase useCase;

    public OrderFacade(OrderUseCase useCase) {
        this.useCase = useCase;
    }

    public OrderResponse create(OrderCreateRequest request, String idempotencyKey, String subject) {
        OrderDomain draft = OrderDomain.created();
        request.items().forEach(item -> draft.addItem(new OrderItemDomain(item.productId(), item.quantity(), null)));
        return OrderResponse.from(useCase.create(draft, new OrderIdempotencyDomain(subject, idempotencyKey)));
    }

    public OrderResponse get(Long id) {
        return OrderResponse.from(useCase.get(id));
    }

    public Page<OrderResponse> list(Pageable pageable) {
        PageResult<OrderDomain> page = useCase.list(new PageQuery(pageable.getPageNumber(), Math.min(pageable.getPageSize(), 100), "createdAt", "DESC"));
        return new PageImpl<>(page.content().stream().map(OrderResponse::from).toList(), PageRequest.of(page.page(), page.size()), page.totalElements());
    }
}
