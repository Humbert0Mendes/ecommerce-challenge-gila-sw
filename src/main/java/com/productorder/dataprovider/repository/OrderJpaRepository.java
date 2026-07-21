package com.productorder.dataprovider.repository;

import com.productorder.dataprovider.entity.OrderEntity;

import java.util.Optional;

import com.productorder.core.domain.order.OrderStatusEnum;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrderJpaRepository extends JpaRepository<OrderEntity, Long> {

    @Override
    @EntityGraph(attributePaths = {"items", "items.product"})
    Optional<OrderEntity> findById(Long id);

    @EntityGraph(attributePaths = {"items", "items.product"})
    Page<OrderEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE OrderEntity orderEntity SET orderEntity.status = :status WHERE orderEntity.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") OrderStatusEnum status);
}
