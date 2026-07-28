package com.ecshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserActivityReportDTO {

    private long totalUsers;
    private long activeUsers;
    private long adminUsers;
}
