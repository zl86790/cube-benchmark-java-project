package com.ecshop.controller;

import com.ecshop.dto.ApiResponse;
import com.ecshop.dto.InventoryReportDTO;
import com.ecshop.dto.SalesReportDTO;
import com.ecshop.dto.UserActivityReportDTO;
import com.ecshop.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/sales")
    public ApiResponse<SalesReportDTO> getSalesReport() {
        return ApiResponse.success(reportService.getSalesReport());
    }

    @GetMapping("/inventory")
    public ApiResponse<InventoryReportDTO> getInventoryReport() {
        return ApiResponse.success(reportService.getInventoryReport());
    }

    @GetMapping("/users")
    public ApiResponse<UserActivityReportDTO> getUserActivityReport() {
        return ApiResponse.success(reportService.getUserActivityReport());
    }
}
