package com.ecshop.controller;

import com.ecshop.dto.ApiResponse;
import com.ecshop.dto.ShipmentDTO;
import com.ecshop.model.Shipment;
import com.ecshop.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/shipments")
@RequiredArgsConstructor
public class ShipmentController {
    private final ShipmentService shipmentService;

    @PostMapping
    public ApiResponse<ShipmentDTO> createShipment(@RequestParam Long orderId, @RequestParam Long carrierId) {
        Shipment shipment = shipmentService.createShipment(orderId, carrierId);
        return ApiResponse.success(shipmentService.toDTO(shipment));
    }

    @GetMapping("/order/{orderId}")
    public ApiResponse<ShipmentDTO> getByOrder(@PathVariable Long orderId) {
        Shipment shipment = shipmentService.getByOrderId(orderId);
        return ApiResponse.success(shipmentService.toDTO(shipment));
    }

    @PostMapping("/{id}/events")
    public ApiResponse<ShipmentDTO> addEvent(
            @PathVariable Long id,
            @RequestParam String location,
            @RequestParam String description) {
        Shipment shipment = shipmentService.addTrackingEvent(id, location, description);
        return ApiResponse.success(shipmentService.toDTO(shipment));
    }

    @PostMapping("/{id}/deliver")
    public ApiResponse<ShipmentDTO> markDelivered(@PathVariable Long id) {
        Shipment shipment = shipmentService.markDelivered(id);
        return ApiResponse.success(shipmentService.toDTO(shipment));
    }
}
