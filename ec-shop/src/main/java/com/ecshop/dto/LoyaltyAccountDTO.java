package com.ecshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyAccountDTO {
    private Long id;
    private Long userId;
    private Long pointsBalance;
    private Long lifetimePoints;
}
