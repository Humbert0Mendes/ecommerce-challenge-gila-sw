package com.productorder.core.usecase.order;

import com.productorder.core.domain.order.OrderDomain;
import com.productorder.core.domain.order.OrderItemDomain;
import com.productorder.core.domain.product.ProductDomain;
import com.productorder.core.exception.BusinessRuleException;
import com.productorder.core.exception.NotFoundException;
import com.productorder.core.gateway.PageQuery;
import com.productorder.core.gateway.PageResult;
import com.productorder.core.gateway.OrderGateway;
import com.productorder.core.gateway.ProductGateway;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderUseCase extends AbstractOrderUseCase {

    public OrderUseCase(OrderGateway orders, ProductGateway products) {
        super(orders, products);
    }

    @Transactional
    public OrderDomain create(OrderDomain orderDomainCommand) {
        Map<Long, Integer> q = new LinkedHashMap<>();
        orderDomainCommand.getItems().forEach(i -> q.merge(i.productId(), i.quantity(), Integer::sum));
        OrderDomain orderDomain = OrderDomain.pending();
        q.forEach((id, n) -> {
            ProductDomain productDomain = products.findActiveByIdForUpdate(id).orElseThrow(() -> new NotFoundException("Product with id " + id + " not found"));
            if (productDomain.getStock() < n) throw new BusinessRuleException("Stock insufficient");
            productDomain.decreaseStock(n);
            products.save(productDomain);
            orderDomain.addItem(new OrderItemDomain(id, n, productDomain.getPrice()));
        });
        return orders.save(orderDomain);
    }

    @Transactional(readOnly = true)
    public OrderDomain get(Long id) {
        return orders.findById(id).orElseThrow(() -> new NotFoundException("Order with id " + id + " not found"));
    }

    @Transactional(readOnly = true)
    public PageResult<OrderDomain> list(PageQuery q) {
        return orders.findAll(q);
    }
}
