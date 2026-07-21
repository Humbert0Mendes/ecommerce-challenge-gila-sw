package com.productorder.core.domain.product;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ProductDomain {

    private Long id;
    private String name;
    private String sku;
    private String description;
    private String category;
    private BigDecimal price;
    private int stock;
    private BigDecimal weightKg;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ProductDomain(Long id, String name, String sku, String description, String category, BigDecimal price, int stock, BigDecimal weightKg, boolean active, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.sku = sku;
        this.description = description;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.weightKg = weightKg;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ProductDomain create(String name, String sku, String description, String category, BigDecimal price, int stock, BigDecimal weightKg) {
        return new ProductDomain(null, name, sku, description, category, price, stock, weightKg, true, null, null);
    }

    public void update(String name, String sku, String description, String category, BigDecimal price, int stock, BigDecimal weightKg) {
        this.name = name;
        this.sku = sku;
        this.description = description;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.weightKg = weightKg;
    }

    public void deactivate() {
        active = false;
    }

    public void reactivate() {
        active = true;
    }

    public void decreaseStock(int quantity) {
        stock -= quantity;
    }

    public void increaseStock(int quantity) {
        stock += quantity;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSku() {
        return sku;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public boolean isActive() {
        return active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
