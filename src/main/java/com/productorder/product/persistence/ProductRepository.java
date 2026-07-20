package com.productorder.product.persistence;

import com.productorder.product.domain.Product;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findBySku(String sku);
    Optional<Product> findByIdAndActiveTrue(Long id);
    Page<Product> findAllByActiveTrue(Pageable pageable);
    @Query("select p from Product p where p.active = true and (lower(p.name) like lower(concat('%', :query, '%')) or lower(p.description) like lower(concat('%', :query, '%')) or lower(p.sku) like lower(concat('%', :query, '%')))")
    Page<Product> searchActive(String query, Pageable pageable);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Product p where p.id = :id and p.active = true")
    Optional<Product> findActiveByIdForUpdate(Long id);
}
