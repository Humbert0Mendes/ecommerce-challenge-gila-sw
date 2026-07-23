package com.productorder.dataprovider.gateway;

import static org.assertj.core.api.Assertions.assertThat;

import com.productorder.core.domain.order.OrderDomain;
import com.productorder.core.domain.order.OrderItemDomain;
import com.productorder.core.domain.product.ProductDomain;
import com.productorder.core.domain.product.ProductFilterDomain;
import com.productorder.core.gateway.PageQuery;
import com.productorder.core.gateway.PageResult;
import com.productorder.dataprovider.repository.OrderJpaRepository;
import com.productorder.dataprovider.repository.ProductJpaRepository;

import java.math.BigDecimal;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@Testcontainers
class DatabaseGatewayIT {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @DynamicPropertySource
    static void datasource(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ProductDatabaseGateway products;

    @Autowired
    private OrderDatabaseGateway orders;

    @Autowired
    private ProductJpaRepository productRepository;

    @Autowired
    private OrderJpaRepository orderRepository;

    @AfterEach
    void cleanDatabase() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
    }

    @Test
    void shouldPersistAndFilterOnlyActiveProducts() {
        var mouse = products.save(product("Mouse sem fio", "MOUSE-001", "Perifericos", "99.90", "0.120"));
        products.save(product("Teclado", "KEYBOARD-001", "Perifericos", "199.90", "0.700"));
        var inactive = product("Mouse antigo", "MOUSE-OLD", "Perifericos", "49.90", "0.100");
        inactive.deactivate();
        products.save(inactive);

        ProductFilterDomain filter = new ProductFilterDomain("mouse", null, "perifericos", new BigDecimal("50.00"), new BigDecimal("100.00"), new BigDecimal("0.100"), new BigDecimal("0.200"));
        PageResult<ProductDomain> result = products.findActive(filter, new PageQuery(0, 20, "name", "ASC"));

        assertThat(result.content()).extracting(ProductDomain::getSku).containsExactly("MOUSE-001");
        assertThat(products.findActiveById(inactive.getId())).isEmpty();
        assertThat(products.findActiveById(mouse.getId())).isPresent();
    }

    @Test
    void shouldPersistOrderWithItemAndReturnItFromGateway() {
        var product = products.save(product("Mouse sem fio", "MOUSE-001", "Perifericos", "99.90", "0.120"));
        var draft = OrderDomain.created();
        draft.addItem(new OrderItemDomain(product.getId(), product.getName(), 2, product.getPrice()));

        var saved = orders.create(draft);
        var found = orders.findById(saved.getId()).orElseThrow();
        PageResult<OrderDomain> page = orders.findAll(new PageQuery(0, 20, "createdAt", "DESC"));

        assertThat(found.getItems()).containsExactly(new OrderItemDomain(product.getId(), "Mouse sem fio", 2, new BigDecimal("99.90")));
        assertThat(found.total()).isEqualByComparingTo("199.80");
        assertThat(page.content()).extracting(OrderDomain::getId).containsExactly(saved.getId());
    }

    private ProductDomain product(String name, String sku, String category, String price, String weight) {
        return ProductDomain.create(name, sku, name + " description", category, new BigDecimal(price), 10, new BigDecimal(weight));
    }
}
