package com.ecshop.service;

import com.ecshop.dto.InventoryReportDTO;
import com.ecshop.dto.SalesReportDTO;
import com.ecshop.dto.UserActivityReportDTO;
import com.ecshop.model.Inventory;
import com.ecshop.model.Order;
import com.ecshop.model.User;
import com.ecshop.repository.InventoryRepository;
import com.ecshop.repository.OrderRepository;
import com.ecshop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService {
    private final OrderRepository orderRepository;
    private final InventoryRepository inventoryRepository;
    private final UserRepository userRepository;

    public SalesReportDTO getSalesReport() {
        List<Order> orders = orderRepository.findAll();

        long totalOrders = orders.size();
        long cancelledOrders = orders.stream()
                .filter(o -> o.getStatus() == Order.OrderStatus.CANCELLED)
                .count();

        BigDecimal totalRevenue = orders.stream()
                .filter(o -> o.getStatus() != Order.OrderStatus.CANCELLED)
                .map(Order::getTotalAmount)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averageOrderValue = totalOrders > 0
                ? totalRevenue.divide(BigDecimal.valueOf(totalOrders), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new SalesReportDTO(totalOrders, totalRevenue, averageOrderValue, cancelledOrders);
    }

    public InventoryReportDTO getInventoryReport() {
        List<Inventory> inventories = inventoryRepository.findAll();

        long lowStock = inventories.stream()
                .filter(i -> i.getMinimumStock() != null
                        && i.getAvailableQuantity() <= i.getMinimumStock())
                .count();
        long outOfStock = inventories.stream()
                .filter(i -> i.getAvailableQuantity() != null && i.getAvailableQuantity() == 0)
                .count();
        int totalUnits = inventories.stream()
                .mapToInt(i -> i.getAvailableQuantity() != null ? i.getAvailableQuantity() : 0)
                .sum();

        return new InventoryReportDTO(inventories.size(), lowStock, outOfStock, totalUnits);
    }

    public UserActivityReportDTO getUserActivityReport() {
        List<User> users = userRepository.findAll();

        long activeUsers = users.stream().filter(User::getIsActive).count();
        long adminUsers = users.stream().filter(u -> u.getRole() == User.UserRole.ADMIN).count();

        return new UserActivityReportDTO(users.size(), activeUsers, adminUsers);
    }
}
