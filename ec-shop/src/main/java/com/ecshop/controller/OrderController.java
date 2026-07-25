package com.ecshop.controller;

import com.ecshop.dto.ApiResponse;
import com.ecshop.dto.OrderDTO;
import com.ecshop.model.Order;
import com.ecshop.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ApiResponse<OrderDTO> createOrder(
            @RequestParam Long userId,
            @RequestParam Long addressId,
            @RequestParam(required = false) String discountCode,
            @RequestParam(required = false) String notes) {
        Order order = orderService.createOrder(userId, addressId, discountCode, notes);
        return ApiResponse.success(orderService.toDTO(order));
    }

    @GetMapping("/{id}")
    public ApiResponse<OrderDTO> getOrder(@PathVariable Long id) {
        Order order = orderService.getOrder(id);
        return ApiResponse.success(orderService.toDTO(order));
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<Page<OrderDTO>> getUserOrders(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<Order> orders = orderService.getUserOrders(userId, page, size);
        Page<OrderDTO> dtoPage = orders.map(orderService::toDTO);
        return ApiResponse.success(dtoPage);
    }

    @PostMapping("/{id}/cancel")
    public ApiResponse<OrderDTO> cancelOrder(@PathVariable Long id) {
        Order order = orderService.cancelOrder(id);
        return ApiResponse.success(orderService.toDTO(order));
    }
}
