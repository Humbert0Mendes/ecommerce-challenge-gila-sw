package com.productorder.dataprovider.repository;

import com.productorder.dataprovider.entity.ProductEntity;

import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {
    Optional<ProductEntity> findBySku(String sku);

    Optional<ProductEntity> findByIdAndActiveTrue(Long id);

    Page<ProductEntity> findAllByActiveTrue(Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from ProductEntity p where p.id=:id and p.active=true")
    Optional<ProductEntity> findActiveByIdForUpdate(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from ProductEntity p where p.id=:id")
    Optional<ProductEntity> findByIdForUpdate(Long id);
}
