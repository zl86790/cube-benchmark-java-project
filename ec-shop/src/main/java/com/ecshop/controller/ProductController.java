package com.ecshop.controller;

import com.ecshop.dto.ApiResponse;
import com.ecshop.dto.ProductDTO;
import com.ecshop.model.Product;
import com.ecshop.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ApiResponse<List<ProductDTO>> getProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        // BUG #8 (MEDIUM): Pagination off-by-one in ProductService.getProducts()
        // The service uses 'page' directly as zero-based index, but users expect
        // page=1 to be the first page. So page=1 returns the second page.
        // COMPILE ERROR #5: getProducts requires 2 arguments (page, size), only 1 passed
        Page<Product> productPage = productService.getProducts(page);
        List<ProductDTO> dtos = productService.toDTOList(productPage.getContent());
        return ApiResponse.success(dtos);
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductDTO> getProduct(@PathVariable Long id) {
        Product product = productService.getProduct(id);
        return ApiResponse.success(productService.toDTO(product));
    }

    @GetMapping("/search")
    public ApiResponse<List<ProductDTO>> searchProducts(@RequestParam String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        return ApiResponse.success(productService.toDTOList(products));
    }

    // BUG #2 (HIGH): SQL Injection endpoint
    @GetMapping("/advanced-search")
    public ApiResponse<List<ProductDTO>> advancedSearch(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String minPrice,
            @RequestParam(required = false) String maxPrice) {
        List<Product> products = productService.advancedSearch(name, category, minPrice, maxPrice);
        return ApiResponse.success(productService.toDTOList(products));
    }

    @PostMapping
    public ApiResponse<ProductDTO> createProduct(@RequestBody Product product) {
        // BUG #16 (LOW): No validation for product name length
        // A product could be created with an empty name or extremely long name
        Product created = productService.createProduct(product);
        return ApiResponse.success(productService.toDTO(created));
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        Product updated = productService.updateProduct(id, product);
        return ApiResponse.success(productService.toDTO(updated));
    }

    @GetMapping("/category/{categoryId}")
    public ApiResponse<List<ProductDTO>> getProductsByCategory(@PathVariable Long categoryId) {
        List<Product> products = productService.getProductsByCategory(categoryId);
        return ApiResponse.success(productService.toDTOList(products));
    }
}
