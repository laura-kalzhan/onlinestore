package com.laurakalzhan.onlinestore.service;

import com.laurakalzhan.onlinestore.dto.CreateProductRequest;
import com.laurakalzhan.onlinestore.dto.ProductResponse;
import com.laurakalzhan.onlinestore.dto.UpdateProductRequest;
import com.laurakalzhan.onlinestore.entity.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class ProductMapper {

    public Product toEntity(CreateProductRequest request) {
        return new Product(
                request.name().trim(),
                request.description(),
                normalize(request.price()),
                request.category().trim(),
                request.inStock() == null ? Boolean.TRUE : request.inStock()
        );
    }

    public void updateEntity(Product product, UpdateProductRequest request) {
        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setPrice(normalize(request.price()));
        product.setCategory(request.category().trim());
        product.setInStock(request.inStock() == null ? Boolean.TRUE : request.inStock());
    }

    public ProductResponse toResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice(),
                product.getCategory(),
                product.getInStock(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    private BigDecimal normalize(BigDecimal price) {
        return price.setScale(2, RoundingMode.HALF_UP);
    }
}
