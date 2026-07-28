package com.ecshop.controller;

import com.ecshop.dto.ApiResponse;
import com.ecshop.model.AuditLog;
import com.ecshop.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit-logs")
@RequiredArgsConstructor
public class AuditLogController {

    private final AuditLogRepository auditLogRepository;

    @GetMapping
    public ApiResponse<List<AuditLog>> getAllLogs() {
        return ApiResponse.success(auditLogRepository.findAllByOrderByCreatedAtDesc());
    }

    @GetMapping("/user/{performedBy}")
    public ApiResponse<List<AuditLog>> getLogsByUser(@PathVariable String performedBy) {
        return ApiResponse.success(auditLogRepository.findByPerformedByOrderByCreatedAtDesc(performedBy));
    }
}
