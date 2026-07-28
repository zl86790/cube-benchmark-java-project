package com.ecshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMembershipDTO {
    private Long id;
    private Long userId;
    private String tierName;
    private LocalDateTime joinedAt;
}
