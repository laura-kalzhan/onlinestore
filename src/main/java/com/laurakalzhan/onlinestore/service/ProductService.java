package com.laurakalzhan.onlinestore.service;

import com.laurakalzhan.onlinestore.dto.CreateProductRequest;
import com.laurakalzhan.onlinestore.dto.ProductResponse;
import com.laurakalzhan.onlinestore.dto.UpdateProductRequest;
import com.laurakalzhan.onlinestore.entity.Product;
import com.laurakalzhan.onlinestore.exception.ProductNotFoundException;
import com.laurakalzhan.onlinestore.repository.ProductRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    public List<ProductResponse> findAll() {
        return productRepository.findAll(Sort.by("id")).stream()
                .map(productMapper::toResponse)
                .toList();
    }

    public ProductResponse findById(Long id) {
        return productMapper.toResponse(getProduct(id));
    }

    public List<ProductResponse> findByCategory(String category) {
        return productRepository.findByCategoryIgnoreCaseOrderByIdAsc(category.trim()).stream()
                .map(productMapper::toResponse)
                .toList();
    }

    public List<ProductResponse> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCaseOrderByIdAsc(name.trim()).stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        Product saved = productRepository.save(productMapper.toEntity(request));
        return productMapper.toResponse(saved);
    }

    @Transactional
    public ProductResponse update(Long id, UpdateProductRequest request) {
        Product product = getProduct(id);
        productMapper.updateEntity(product, request);
        return productMapper.toResponse(productRepository.saveAndFlush(product));
    }

    @Transactional
    public void delete(Long id) {
        Product product = getProduct(id);
        productRepository.delete(product);
    }

    private Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }
}
