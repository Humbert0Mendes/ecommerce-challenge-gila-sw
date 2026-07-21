package com.productorder.dataprovider.gateway;

import com.productorder.core.domain.order.OrderDomain;
import com.productorder.core.gateway.OrderGateway;
import com.productorder.core.gateway.PageQuery;
import com.productorder.core.gateway.PageResult;
import com.productorder.dataprovider.entity.OrderEntity;
import com.productorder.dataprovider.mapper.OrderEntityMapper;
import com.productorder.dataprovider.repository.OrderJpaRepository;
import com.productorder.dataprovider.repository.ProductJpaRepository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class OrderDatabaseGateway implements OrderGateway {
    private final OrderJpaRepository orders;
    private final ProductJpaRepository products;
    private final OrderEntityMapper mapper;

    public OrderDatabaseGateway(OrderJpaRepository orders, ProductJpaRepository products, OrderEntityMapper mapper) {
        this.orders = orders;
        this.products = products;
        this.mapper = mapper;
    }

    public OrderDomain save(OrderDomain orderDomain) {
        return mapper.toDomain(orders.save(mapper.toEntity(orderDomain, products::getReferenceById)));
    }

    public Optional<OrderDomain> findById(Long id) {
        return orders.findById(id).map(mapper::toDomain);
    }

    public PageResult<OrderDomain> findAll(PageQuery pageQuery) {
        Page<OrderEntity> p = orders.findAllByOrderByCreatedAtDesc(PageRequest.of(pageQuery.page(), pageQuery.size()));
        return new PageResult<>(p.getContent().stream().map(mapper::toDomain).toList(), p.getNumber(), p.getSize(), p.getTotalElements(), p.getTotalPages());
    }
}
