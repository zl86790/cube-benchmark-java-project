package com.ecshop.controller;

import com.ecshop.dto.ApiResponse;
import com.ecshop.exception.BusinessException;
import com.ecshop.model.Notification;
import com.ecshop.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationRepository notificationRepository;

    @GetMapping("/user/{userId}")
    public ApiResponse<List<Notification>> getUserNotifications(@PathVariable Long userId) {
        return ApiResponse.success(notificationRepository.findByUserIdOrderByCreatedAtDesc(userId));
    }

    @GetMapping("/user/{userId}/unread")
    public ApiResponse<List<Notification>> getUnread(@PathVariable Long userId) {
        return ApiResponse.success(notificationRepository.findByUserIdAndReadFalse(userId));
    }

    @PostMapping("/{id}/read")
    public ApiResponse<Notification> markRead(@PathVariable Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Notification not found: " + id));
        notification.setIsRead(true);
        return ApiResponse.success(notificationRepository.save(notification));
    }
}
