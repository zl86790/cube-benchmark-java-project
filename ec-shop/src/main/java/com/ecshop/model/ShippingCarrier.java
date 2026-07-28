package com.ecshop.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "shipping_carriers")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingCarrier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "tracking_url_template")
    private String trackingUrlTemplate;

    @Column(name = "base_fee")
    private BigDecimal baseFee;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
}
