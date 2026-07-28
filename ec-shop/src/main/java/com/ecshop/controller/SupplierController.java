package com.ecshop.controller;

import com.ecshop.dto.ApiResponse;
import com.ecshop.model.Supplier;
import com.ecshop.service.SupplierService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/suppliers")
@RequiredArgsConstructor
public class SupplierController {

    private final SupplierService supplierService;

    @GetMapping
    public ApiResponse<List<Supplier>> getActiveSuppliers() {
        return ApiResponse.success(supplierService.getActiveSuppliers());
    }

    @GetMapping("/{id}")
    public ApiResponse<Supplier> getSupplier(@PathVariable Long id) {
        return ApiResponse.success(supplierService.getSupplier(id));
    }

    @PostMapping
    public ApiResponse<Supplier> createSupplier(@RequestBody Supplier supplier) {
        return ApiResponse.success(supplierService.createSupplier(supplier));
    }
}
