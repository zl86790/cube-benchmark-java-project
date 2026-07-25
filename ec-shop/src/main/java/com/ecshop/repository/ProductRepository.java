package com.ecshop.repository;

import com.ecshop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(String sku);

    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByNameContainingIgnoreCase(String keyword);

    List<Product> findByIsActiveTrue();

    @Query("SELECT p FROM Product p WHERE p.category.id = :categoryId AND p.isActive = true")
    List<Product> findActiveByCategory(@Param("categoryId") Long categoryId);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchByKeyword(@Param("keyword") String keyword);

    boolean existsBySku(String sku);
}
