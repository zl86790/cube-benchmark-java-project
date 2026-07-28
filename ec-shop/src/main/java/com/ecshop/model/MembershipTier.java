package com.ecshop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "membership_tiers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipTier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "min_spend", nullable = false)
    private BigDecimal minSpend;

    @Column(name = "discount_percentage", nullable = false)
    private BigDecimal discountPercentage;

    @Column
    private String description;
}
