package com.productorder.core.usecase.order;

import com.productorder.core.domain.order.OrderDomain;
import com.productorder.core.domain.order.OrderItemDomain;
import com.productorder.core.domain.order.OrderStatusEnum;
import com.productorder.core.domain.product.ProductDomain;
import com.productorder.core.exception.BusinessRuleException;
import com.productorder.core.exception.NotFoundException;
import com.productorder.core.gateway.OrderGateway;
import com.productorder.core.gateway.ProductGateway;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderPaymentProcessor extends AbstractOrderUseCase {

    public OrderPaymentProcessor(OrderGateway orders, ProductGateway products) {
        super(orders, products);
    }

    @Transactional
    public OrderDomain createAndReserve(OrderDomain draft) {
        Map<Long, Integer> quantities = new LinkedHashMap<>();
        draft.getItems().forEach(item -> quantities.merge(item.productId(), item.quantity(), Integer::sum));
        OrderDomain order = OrderDomain.created();
        quantities.forEach((productId, quantity) -> reserveProduct(order, productId, quantity));
        return orders.save(order);
    }

    @Transactional
    public OrderDomain startProcessing(Long orderId) {
        OrderDomain order = order(orderId);
        order.startProcessing();
        return orders.save(order);
    }

    @Transactional
    public OrderDomain confirm(Long orderId) {
        OrderDomain order = order(orderId);
        order.confirm();
        return orders.save(order);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void declineAndRestore(Long orderId) {
        OrderDomain order = order(orderId);
        if (order.getStatus() == OrderStatusEnum.DECLINED) {
            return;
        }
        order.getItems().forEach(this::restoreProduct);
        order.decline();
        orders.save(order);
    }

    private void reserveProduct(OrderDomain order, Long productId, int quantity) {
        ProductDomain product = products.findActiveByIdForUpdate(productId).orElseThrow(() -> new NotFoundException("Product with id " + productId));
        if (product.getStock() < quantity) {
            throw new BusinessRuleException("Stock insufficient");
        }
        product.decreaseStock(quantity);
        products.save(product);
        order.addItem(new OrderItemDomain(productId, quantity, product.getPrice()));
    }

    private void restoreProduct(OrderItemDomain item) {
        ProductDomain product = products.findByIdForUpdate(item.productId()).orElseThrow(() -> new NotFoundException("Product with id " + item.productId()));
        product.increaseStock(item.quantity());
        products.save(product);
    }

    private OrderDomain order(Long id) {
        return orders.findById(id).orElseThrow(() -> new NotFoundException("Order with id " + id));
    }
}
