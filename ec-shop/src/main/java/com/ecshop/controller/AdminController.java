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

    // BUG #4 (HIGH): Missing authorization check
    // This endpoint should require ADMIN role (@PreAuthorize("hasRole('ADMIN')"))
    // Without it, any authenticated user (or even anonymous) can delete products
    @DeleteMapping("/products/{id}")
    public ApiResponse<Void> deleteProduct(@PathVariable Long id) {
        // Should check if the current user has ADMIN role
        Product product = productService.getProduct(id);
        product.setIsActive(false);
        productService.createProduct(product); // soft delete via update
        auditService.writeAuditLog("PRODUCT_DELETED", "admin", "product:" + id);
        return ApiResponse.success("Product deleted", null);
    }

    // BUG #4 continued: Also missing authorization
    @GetMapping("/users")
    public ApiResponse<List<User>> getAllUsers() {
        // Should require ADMIN role
        return ApiResponse.success(null); // Simplified
    }

    // BUG #4 continued: Missing authorization check
    @PostMapping("/products/{id}/feature")
    public ApiResponse<Void> featureProduct(@PathVariable Long id) {
        Product product = productService.getProduct(id);
        // Logic to feature product
        auditService.writeAuditLog("PRODUCT_FEATURED", "admin", "product:" + id);
        return ApiResponse.success("Product featured", null);
    }

    @GetMapping("/dashboard/stats")
    public ApiResponse<String> getDashboardStats() {
        // Simplified dashboard
        return ApiResponse.success("Dashboard stats retrieved");
    }
}
