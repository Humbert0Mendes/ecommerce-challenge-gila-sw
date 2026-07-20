package com.productorder.product.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, length = 255) private String name;
    @Column(nullable = false, unique = true, length = 20) private String sku;
    @Column(nullable = false) private String description;
    @Column(nullable = false, length = 100) private String category;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal price;
    @Column(nullable = false) private int stock;
    @Column(name = "weight_kg", nullable = false, precision = 10, scale = 3) private BigDecimal weightKg;
    @Column(nullable = false) private boolean active = true;
    @Column(name = "created_at", nullable = false, insertable = false, updatable = false) private LocalDateTime createdAt;
    @Column(name = "updated_at", nullable = false, insertable = false, updatable = false) private LocalDateTime updatedAt;

    protected Product() { }

    public Product(String name, String sku, String description, String category, BigDecimal price, int stock, BigDecimal weightKg) {
        this.name = name;
        this.sku = sku;
        this.description = description;
        this.category = category;
        this.price = price;
        this.stock = stock;
        this.weightKg = weightKg;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSku() { return sku; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public BigDecimal getPrice() { return price; }
    public int getStock() { return stock; }
    public BigDecimal getWeightKg() { return weightKg; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void update(String name, String sku, String description, String category, BigDecimal price, int stock, BigDecimal weightKg) {
        this.name = name; this.sku = sku; this.description = description; this.category = category;
        this.price = price; this.stock = stock; this.weightKg = weightKg;
    }
    public void deactivate() { this.active = false; }
    public void reactivate() { this.active = true; }
    public void decreaseStock(int quantity) { this.stock -= quantity; }
}
