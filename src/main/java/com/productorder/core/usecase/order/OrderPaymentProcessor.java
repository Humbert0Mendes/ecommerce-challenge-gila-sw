package com.productorder.core.usecase.order;

import com.productorder.core.domain.order.OrderDomain;
import com.productorder.core.domain.order.OrderItemDomain;
import com.productorder.core.exception.BusinessRuleException;
import com.productorder.core.exception.NotFoundException;
import com.productorder.core.gateway.OrderGateway;
import com.productorder.core.gateway.ProductGateway;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderPaymentProcessor extends AbstractOrderUseCase {
    public OrderPaymentProcessor(OrderGateway orders, ProductGateway products) {
        super(orders, products);
    }

    @Transactional
    public OrderDomain createAndReserveProcessing(OrderDomain draft) {
        Map<Long, Integer> quantities = new LinkedHashMap<>();
        draft.getItems().forEach(item -> quantities.merge(item.productId(), item.quantity(), Integer::sum));

        var order = OrderDomain.created();
        quantities.forEach((productId, quantity) -> reserve(order, productId, quantity));
        order.startProcessing();

        return orders.create(order);
    }

    private void reserve(OrderDomain order, Long productId, int quantity) {
        var product = products.findActiveById(productId).orElseThrow(()
                -> new NotFoundException("Product with id " + productId + " not found"));

        if (!products.reserveStock(productId, quantity)) {
            throw new BusinessRuleException("Insufficient stock for product " + productId);
        }
        order.addItem(new OrderItemDomain(productId, product.getName(), quantity, product.getPrice()));
    }
}
