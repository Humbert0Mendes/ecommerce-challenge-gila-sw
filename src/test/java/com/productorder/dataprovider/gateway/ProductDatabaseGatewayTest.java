package com.productorder.dataprovider.gateway;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.productorder.core.domain.product.ProductFilterDomain;
import com.productorder.core.gateway.PageQuery;
import com.productorder.dataprovider.entity.ProductEntity;
import com.productorder.dataprovider.mapper.ProductEntityMapper;
import com.productorder.dataprovider.repository.ProductJpaRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class ProductDatabaseGatewayTest {

    @Mock
    private ProductJpaRepository repository;

    @Mock
    private ProductEntityMapper mapper;

    private ProductDatabaseGateway gateway;

    @BeforeEach
    void setUp() {
        gateway = new ProductDatabaseGateway(repository, mapper);
    }

    @Test
    void shouldUseSimpleActiveProductQueryWhenFilterIsEmpty() {
        when(repository.findAllByActiveTrue(any(Pageable.class))).thenReturn(Page.empty());

        gateway.findActive(new ProductFilterDomain(null, null, null, null, null, null, null), new PageQuery(0, 20, "name", "ASC"));

        verify(repository).findAllByActiveTrue(any(Pageable.class));
        verify(repository, never()).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    void shouldUseSpecificationWhenAtLeastOneFilterIsPresent() {
        when(repository.findAll(any(Specification.class), any(Pageable.class))).thenReturn(Page.<ProductEntity>empty());

        gateway.findActive(new ProductFilterDomain("Mouse", null, null, null, null, null, null), new PageQuery(0, 20, "name", "ASC"));

        verify(repository).findAll(any(Specification.class), any(Pageable.class));
        verify(repository, never()).findAllByActiveTrue(any(Pageable.class));
    }
}
