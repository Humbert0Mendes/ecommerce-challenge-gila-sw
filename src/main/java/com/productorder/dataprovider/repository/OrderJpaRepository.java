package com.productorder.dataprovider.repository;
import com.productorder.dataprovider.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
public interface OrderJpaRepository extends JpaRepository<OrderEntity,Long> { Page<OrderEntity> findAllByOrderByCreatedAtDesc(Pageable pageable); }
