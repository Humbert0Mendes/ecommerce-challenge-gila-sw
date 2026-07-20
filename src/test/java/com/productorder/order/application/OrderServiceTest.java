package com.productorder.order.application;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.productorder.order.api.OrderCreateRequest;
import com.productorder.order.api.OrderItemRequest;
import com.productorder.order.persistence.OrderRepository;
import com.productorder.product.domain.Product;
import com.productorder.product.persistence.ProductRepository;
import com.productorder.shared.error.BusinessRuleException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock private OrderRepository orders;
    @Mock private ProductRepository products;
    @InjectMocks private OrderService service;

    @Test
    void rejectsOrderWithInsufficientStock() {
        Product product = new Product("Mouse", "M-1", "Mouse", "Periféricos", BigDecimal.TEN, 1, BigDecimal.ONE);
        when(products.findActiveByIdForUpdate(1L)).thenReturn(Optional.of(product));
        OrderCreateRequest request = new OrderCreateRequest(List.of(new OrderItemRequest(1L, 2)));
        assertThatThrownBy(() -> service.create(request)).isInstanceOf(BusinessRuleException.class).hasMessage("Estoque insuficiente");
    }
}
