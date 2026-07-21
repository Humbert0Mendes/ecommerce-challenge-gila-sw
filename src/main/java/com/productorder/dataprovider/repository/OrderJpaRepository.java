package com.productorder.dataprovider.repository;

import com.productorder.dataprovider.entity.OrderEntity;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {

    @Override
    @EntityGraph(attributePaths = {"items", "items.product"})
    Optional<OrderEntity> findById(Long id);

    @EntityGraph(attributePaths = {"items", "items.product"})
    Page<OrderEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
