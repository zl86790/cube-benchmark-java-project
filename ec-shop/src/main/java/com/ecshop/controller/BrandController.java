package com.ecshop.controller;

import com.ecshop.dto.ApiResponse;
import com.ecshop.model.Brand;
import com.ecshop.service.BrandService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;

    @GetMapping
    public ApiResponse<List<Brand>> getActiveBrands() {
        return ApiResponse.success(brandService.getAllActiveBrands());
    }

    @GetMapping("/{id}")
    public ApiResponse<Brand> getBrand(@PathVariable Long id) {
        return ApiResponse.success(brandService.getBrand(id));
    }

    @PostMapping
    public ApiResponse<Brand> createBrand(@RequestBody Brand brand) {
        return ApiResponse.success(brandService.createBrand(brand));
    }
}
