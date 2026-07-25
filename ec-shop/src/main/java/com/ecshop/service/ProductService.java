package com.ecshop.service;

import com.ecshop.dto.ProductDTO;
import com.ecshop.exception.BusinessException;
import com.ecshop.model.Product;
import com.ecshop.repository.ProductRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {

    private final ProductRepository productRepository;

    @PersistenceContext
    private EntityManager entityManager;

    public Page<Product> getProducts(int page, int size) {
        // BUG #8 (MEDIUM): Pagination off-by-one - should be page-1 but using page directly
        // For page=1, we want first page (index 0), but PageRequest.of(page, size)
        // treats page directly as zero-based index, so page=1 gives SECOND page
        return productRepository.findAll(PageRequest.of(page, size));
    }

    @Cacheable(value = "products", key = "#id")
    public Product getProduct(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Product not found with id: " + id));
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.searchByKeyword(keyword);
    }

    // BUG #2 (HIGH): SQL Injection via raw SQL concatenation
    @SuppressWarnings("unchecked")
    public List<Product> advancedSearch(String name, String category, String minPrice, String maxPrice) {
        StringBuilder sql = new StringBuilder("SELECT * FROM products WHERE 1=1");

        if (name != null && !name.isEmpty()) {
            sql.append(" AND name LIKE '%").append(name).append("%'");
        }
        if (category != null && !category.isEmpty()) {
            sql.append(" AND category_id = ").append(category);
        }
        if (minPrice != null && !minPrice.isEmpty()) {
            sql.append(" AND price >= ").append(minPrice);
        }
        if (maxPrice != null && !maxPrice.isEmpty()) {
            sql.append(" AND price <= ").append(maxPrice);
        }

        log.info("Executing advanced search SQL: {}", sql.toString());
        return entityManager.createNativeQuery(sql.toString(), Product.class).getResultList();
    }

    @Transactional
    public Product createProduct(Product product) {
        // BUG #20 (LOW): No duplicate SKU check before saving
        // Allows duplicate SKU to be inserted, causing constraint violation
        return productRepository.save(product);
    }

    @Transactional
    public Product updateProduct(Long id, Product updated) {
        Product existing = getProduct(id);
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPrice(updated.getPrice());
        existing.setStockQuantity(updated.getStockQuantity());
        existing.setCategory(updated.getCategory());
        existing.setImageUrl(updated.getImageUrl());
        existing.setIsActive(updated.getIsActive());
        // BUG #9 (MEDIUM): Cache not invalidated after update
        // @CachePut should be used or @CacheEvict manually
        return productRepository.save(existing);
    }

    public List<Product> getProductsByCategory(Long categoryId) {
        return productRepository.findActiveByCategory(categoryId);
    }

    public ProductDTO toDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setSku(product.getSku());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setPrice(product.getPrice());
        dto.setStockQuantity(product.getStockQuantity());
        if (product.getCategory() != null) {
            dto.setCategoryId(product.getCategory().getId());
            dto.setCategoryName(product.getCategory().getName());
        }
        dto.setImageUrl(product.getImageUrl());
        dto.setIsActive(product.getIsActive());
        return dto;
    }

    public List<ProductDTO> toDTOList(List<Product> products) {
        return products.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
