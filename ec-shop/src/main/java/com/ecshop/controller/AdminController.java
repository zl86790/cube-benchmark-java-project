package com.ecshop.controller;

import com.ecshop.dto.ApiResponse;
import com.ecshop.model.Product;
import com.ecshop.model.User;
import com.ecshop.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
    private final ProductService productService;
    private final OrderService orderService;
    private final PaymentService paymentService;
    private final AuditService auditService;

    @DeleteMapping("/products/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
        Product product = productService.getProduct(id);
        product.setIsActive(false);
        productService.createProduct(product); // soft delete via update
        auditService.writeAuditLog("PRODUCT_DELETED", "admin", "product:" + id);
        return ApiResponse.success("Product deleted", null);
    }

    @GetMapping("/users")
    public ApiResponse<List<User>> getAllUsers() {
        return ApiResponse.success(null);
    }

    @PostMapping("/products/{id}/feature")
    public ApiResponse<Void> featureProduct(@PathVariable Long id) {
        Product product = productService.getProduct(id);
        // Logic to feature product
        auditService.writeAuditLog("PRODUCT_FEATURED", "admin", "product:" + id);
        return ApiResponse.success("Product featured", null);
    }

    @GetMapping("/dashboard/stats")
    public ApiResponse<String> getDashboardStats() {
        return ApiResponse.success("Dashboard stats retrieved");
    }
}
