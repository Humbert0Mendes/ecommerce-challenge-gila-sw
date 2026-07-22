package com.productorder.dataprovider.mapper;

import com.productorder.core.domain.order.OrderDomain;
import com.productorder.core.domain.order.OrderItemDomain;
import com.productorder.dataprovider.entity.OrderEntity;
import com.productorder.dataprovider.entity.OrderItemEntity;
import com.productorder.dataprovider.entity.ProductEntity;

import org.springframework.stereotype.Component;

@Component
public class OrderEntityMapper {
    public OrderDomain toDomain(OrderEntity orderEntity) {
        return new OrderDomain(orderEntity.getId(), orderEntity.getCreatedAt(), orderEntity.getStatus(), orderEntity.getItems().stream().map(i
                -> new OrderItemDomain(i.getProduct().getId(), i.getProductName(), i.getQuantity(), i.getUnitPrice())).toList());
    }

    public OrderEntity toEntity(OrderDomain orderDomain, java.util.function.Function<Long, ProductEntity> product) {
        OrderEntity orderEntity = new OrderEntity(orderDomain.getStatus(), orderDomain.total());
        orderDomain.getItems().forEach(i
                -> orderEntity.addItem(new OrderItemEntity(product.apply(i.productId()), i.productName(), i.quantity(), i.unitPrice())));
        return orderEntity;
    }

}
