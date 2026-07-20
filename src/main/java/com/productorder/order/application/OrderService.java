package com.productorder.order.application;

import com.productorder.order.api.OrderCreateRequest;
import com.productorder.order.api.OrderItemRequest;
import com.productorder.order.api.OrderResponse;
import com.productorder.order.domain.Order;
import com.productorder.order.domain.OrderItem;
import com.productorder.order.persistence.OrderRepository;
import com.productorder.product.domain.Product;
import com.productorder.product.persistence.ProductRepository;
import com.productorder.shared.error.BusinessRuleException;
import com.productorder.shared.error.NotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final OrderRepository orders;
    private final ProductRepository products;
    public OrderService(OrderRepository orders, ProductRepository products) { this.orders = orders; this.products = products; }
    @Transactional
    public OrderResponse create(OrderCreateRequest request) {
        Map<Long, Integer> quantities = new LinkedHashMap<>();
        for (OrderItemRequest item : request.items()) quantities.merge(item.productId(), item.quantity(), Integer::sum);
        Order order = Order.pending();
        for (Map.Entry<Long, Integer> entry : quantities.entrySet()) {
            Product product = products.findActiveByIdForUpdate(entry.getKey()).orElseThrow(() -> new NotFoundException("Produto"));
            if (product.getStock() < entry.getValue()) throw new BusinessRuleException("Estoque insuficiente");
            product.decreaseStock(entry.getValue());
            order.addItem(new OrderItem(product, entry.getValue(), product.getPrice()));
        }
        return OrderResponse.from(orders.save(order));
    }
    @Transactional(readOnly = true)
    public OrderResponse get(Long id) { return OrderResponse.from(orders.findById(id).orElseThrow(() -> new NotFoundException("Pedido"))); }
    @Transactional(readOnly = true)
    public Page<OrderResponse> list(Pageable pageable) { return orders.findAllByOrderByCreatedAtDesc(pageable).map(OrderResponse::from); }
}
