package com.productorder.dataprovider.mapper;
import com.productorder.core.domain.product.ProductDomain;
import com.productorder.dataprovider.entity.ProductEntity;
import org.springframework.stereotype.Component;
@Component public class ProductEntityMapper {
 public ProductDomain toDomain(ProductEntity e){return new ProductDomain(e.getId(),e.getName(),e.getSku(),e.getDescription(),e.getCategory(),e.getPrice(),e.getStock(),e.getWeightKg(),e.isActive(),e.getCreatedAt(),e.getUpdatedAt());}
 public ProductEntity toEntity(ProductDomain p){return new ProductEntity(p.getName(),p.getSku(),p.getDescription(),p.getCategory(),p.getPrice(),p.getStock(),p.getWeightKg(),p.isActive());}
 public void update(ProductDomain p, ProductEntity e){e.update(p.getName(),p.getSku(),p.getDescription(),p.getCategory(),p.getPrice(),p.getStock(),p.getWeightKg(),p.isActive());}
}
