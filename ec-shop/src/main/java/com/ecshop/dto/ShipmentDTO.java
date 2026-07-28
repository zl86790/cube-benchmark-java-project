package com.ecshop.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShipmentDTO {

    private Long id;
    private Long orderId;
    private String carrierName;
    private String trackingNumber;
    private String status;
    private List<TrackingEventDTO> events;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
}
