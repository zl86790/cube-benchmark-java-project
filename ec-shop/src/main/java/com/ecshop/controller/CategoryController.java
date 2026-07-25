package com.ecshop.controller;

import com.ecshop.dto.ApiResponse;
import com.ecshop.model.Category;
import com.ecshop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ApiResponse<List<Category>> getAllCategories() {
        return ApiResponse.success(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    public ApiResponse<Category> getCategory(@PathVariable Long id) {
        return ApiResponse.success(categoryService.getCategory(id));
    }

    @GetMapping("/{id}/subcategories")
    public ApiResponse<List<Category>> getSubCategories(@PathVariable Long id) {
        return ApiResponse.success(categoryService.getSubCategories(id));
    }

    @PostMapping
    public ApiResponse<Category> createCategory(@RequestBody Category category) {
        return ApiResponse.success(categoryService.createCategory(category));
    }

    @PutMapping("/{id}")
    public ApiResponse<Category> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        return ApiResponse.success(categoryService.updateCategory(id, category));
    }
}
