package com.productorder.dataprovider.repository;

import com.productorder.dataprovider.entity.ProductEntity;

import java.util.Optional;
import java.util.List;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;

public interface ProductJpaRepository extends JpaRepository<ProductEntity, Long>, JpaSpecificationExecutor<ProductEntity> {
    Optional<ProductEntity> findBySku(String sku);

    Optional<ProductEntity> findByIdAndActiveTrue(Long id);

    Page<ProductEntity> findAllByActiveTrue(Pageable pageable);

    @Query("select distinct p.category from ProductEntity p where p.active = true order by p.category")
    List<String> findDistinctActiveCategories();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from ProductEntity p where p.id=:id and p.active=true")
    Optional<ProductEntity> findActiveByIdForUpdate(Long id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from ProductEntity p where p.id=:id")
    Optional<ProductEntity> findByIdForUpdate(Long id);

    @Modifying
    @Query("update ProductEntity p set p.stock = p.stock - :quantity where p.id = :productId and p.active = true and p.stock >= :quantity")
    int reserveStock(@Param("productId") Long productId, @Param("quantity") int quantity);

    @Modifying
    @Query("update ProductEntity p set p.stock = p.stock + :quantity where p.id = :productId")
    int releaseStock(@Param("productId") Long productId, @Param("quantity") int quantity);
}
