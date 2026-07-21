package com.productorder.entrypoint.api.facade;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.productorder.core.domain.order.OrderDomain;
import com.productorder.core.domain.order.OrderIdempotencyDomain;
import com.productorder.core.domain.order.OrderItemDomain;
import com.productorder.core.domain.order.OrderStatusEnum;
import com.productorder.core.gateway.PageQuery;
import com.productorder.core.gateway.PageResult;
import com.productorder.core.usecase.order.OrderUseCase;
import com.productorder.entrypoint.api.dto.PageResponse;
import com.productorder.entrypoint.api.dto.order.OrderCreateRequest;
import com.productorder.entrypoint.api.dto.order.OrderItemRequest;
import com.productorder.entrypoint.api.dto.order.OrderResponse;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class OrderFacadeTest {

    @Mock
    private OrderUseCase useCase;

    @Captor
    private ArgumentCaptor<OrderDomain> orderCaptor;

    @Captor
    private ArgumentCaptor<PageQuery> pageCaptor;

    private OrderFacade facade;

    @BeforeEach
    void setUp() {
        facade = new OrderFacade(useCase);
    }

    @Test
    void shouldMapOrderRequestToCreatedDomain() {
        OrderCreateRequest request = new OrderCreateRequest(List.of(new OrderItemRequest(1L, 2)));
        OrderDomain saved = new OrderDomain(1L, null, OrderStatusEnum.CONFIRMED, List.of(new OrderItemDomain(1L, 2, new BigDecimal("19.90"))));
        when(useCase.create(org.mockito.ArgumentMatchers.any(OrderDomain.class), org.mockito.ArgumentMatchers.any(OrderIdempotencyDomain.class))).thenReturn(saved);

        OrderResponse response = facade.create(request, "key-1", "tester");

        verify(useCase).create(orderCaptor.capture(), org.mockito.ArgumentMatchers.eq(new OrderIdempotencyDomain("tester", "key-1")));
        assertThat(orderCaptor.getValue().getStatus()).isEqualTo(OrderStatusEnum.CREATED);
        assertThat(orderCaptor.getValue().getItems()).containsExactly(new OrderItemDomain(1L, 2, null));
        assertThat(response.total()).isEqualByComparingTo("39.80");
    }

    @Test
    void shouldUseFixedCreatedAtDescendingSortWhenListingOrders() {
        PageResult<OrderDomain> result = new PageResult<>(List.of(OrderDomain.created()), 0, 100, 1, 1);
        when(useCase.list(org.mockito.ArgumentMatchers.any(PageQuery.class))).thenReturn(result);

        PageResponse<OrderResponse> response = facade.list(PageRequest.of(0, 200));

        verify(useCase).list(pageCaptor.capture());
        assertThat(pageCaptor.getValue()).isEqualTo(new PageQuery(0, 100, "createdAt", "DESC"));
        assertThat(response.totalElements()).isOne();
    }
}
