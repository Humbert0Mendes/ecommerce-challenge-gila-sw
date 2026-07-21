package com.productorder.core.usecase.order;

import com.productorder.core.domain.order.OrderDomain;
import com.productorder.core.domain.order.OrderItemDomain;
import com.productorder.core.domain.order.OrderStatusEnum;
import com.productorder.core.exception.BusinessRuleException;
import com.productorder.core.exception.NotFoundException;
import com.productorder.core.gateway.OrderGateway;
import com.productorder.core.gateway.ProductGateway;

import java.util.LinkedHashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderPaymentProcessor extends AbstractOrderUseCase {

    private static final Logger log = LoggerFactory.getLogger(OrderPaymentProcessor.class);

    public OrderPaymentProcessor(OrderGateway orders, ProductGateway products) {
        super(orders, products);
    }

    @Transactional
    public OrderDomain createAndReserve(OrderDomain draft) {
        Map<Long, Integer> quantities = new LinkedHashMap<>();
        draft.getItems().forEach(item -> quantities.merge(item.productId(), item.quantity(), Integer::sum));
        var order = OrderDomain.created();
        quantities.forEach((productId, quantity) -> reserveProduct(order, productId, quantity));
        return orders.create(order);
    }

    @Transactional
    public OrderDomain startProcessing(Long orderId) {
        var order = order(orderId);
        order.startProcessing();
        return orders.update(order);
    }

    @Transactional
    public OrderDomain confirm(Long orderId) {
        var order = order(orderId);
        order.confirm();
        return orders.update(order);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void declineAndRestore(Long orderId) {
        var order = order(orderId);
        if (order.getStatus() == OrderStatusEnum.DECLINED) {
            return;
        }
        order.getItems().forEach(this::restoreProduct);
        order.decline();
        orders.update(order);
    }

    private void reserveProduct(OrderDomain order, Long productId, int quantity) {
        var product = products.findActiveByIdForUpdate(productId).orElseThrow(() -> new NotFoundException("Product with id " + productId));
        if (product.getStock() < quantity) {
            var messageError = String.format("Stock for product %s is less than the requested quantity %s", productId, quantity);
            log.error(messageError);
            throw new BusinessRuleException(messageError);
        }
        product.decreaseStock(quantity);
        products.save(product);
        order.addItem(new OrderItemDomain(productId, quantity, product.getPrice()));
    }

    private void restoreProduct(OrderItemDomain item) {
        var product = products.findByIdForUpdate(item.productId()).orElseThrow(() -> new NotFoundException("Product with id " + item.productId()));
        product.increaseStock(item.quantity());
        products.save(product);
    }

    private OrderDomain order(Long id) {
        return orders.findById(id).orElseThrow(() -> new NotFoundException("Order with id " + id));
    }
}
