package com.ecshop.service;

import com.ecshop.dto.ShipmentDTO;
import com.ecshop.dto.TrackingEventDTO;
import com.ecshop.exception.BusinessException;
import com.ecshop.exception.ShipmentNotFoundException;
import com.ecshop.model.Shipment;
import com.ecshop.model.ShippingCarrier;
import com.ecshop.model.TrackingEvent;
import com.ecshop.repository.ShipmentRepository;
import com.ecshop.repository.ShippingCarrierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ShippingCarrierRepository shippingCarrierRepository;

    @Transactional
    public Shipment createShipment(Long orderId, Long carrierId) {
        ShippingCarrier carrier = shippingCarrierRepository.findById(carrierId)
                .orElseThrow(() -> new BusinessException("Shipping carrier not found: " + carrierId));

        Shipment shipment = new Shipment();
        shipment.setOrderId(orderId);
        shipment.setCarrier(carrier);
        shipment.setTrackingNumber("TRK-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        shipment.setStatus(Shipment.ShipmentStatus.PREPARING);
        return shipmentRepository.save(shipment);
    }

    public Shipment getByOrderId(Long orderId) {
        return shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new ShipmentNotFoundException(orderId));
    }

    @Transactional
    public Shipment addTrackingEvent(Long shipmentId, String location, String description) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new BusinessException("Shipment not found: " + shipmentId));

        TrackingEvent event = new TrackingEvent();
        event.setShipment(shipment);
        event.setLocation(location);
        event.setDescription(description);
        shipment.getEvents().add(event);

        if (shipment.getStatus() == Shipment.ShipmentStatus.PREPARING) {
            shipment.setStatus(Shipment.ShipmentStatus.SHIPPED);
            shipment.setShippedAt(LocalDateTime.now());
        } else {
            shipment.setStatus(Shipment.ShipmentStatus.IN_TRANSIT);
        }

        return shipmentRepository.save(shipment);
    }

    @Transactional
    public Shipment markDelivered(Long shipmentId) {
        Shipment shipment = shipmentRepository.findById(shipmentId)
                .orElseThrow(() -> new BusinessException("Shipment not found: " + shipmentId));
        shipment.setStatus(Shipment.ShipmentStatus.DELIVERED);
        shipment.setDeliveredAt(LocalDateTime.now());
        return shipmentRepository.save(shipment);
    }

    public ShipmentDTO toDTO(Shipment shipment) {
        ShipmentDTO dto = new ShipmentDTO();
        dto.setId(shipment.getId());
        dto.setOrderId(shipment.getOrderId());
        dto.setCarrierName(shipment.getCarrier().getName());
        dto.setTrackingNumber(shipment.getTrackingNumber());
        dto.setStatus(shipment.getStatus().name());
        dto.setShippedAt(shipment.getShippedAt());
        dto.setDeliveredAt(shipment.getDeliveredAt());
        dto.setEvents(shipment.getEvents().stream().map(event -> {
            TrackingEventDTO eventDTO = new TrackingEventDTO();
            eventDTO.setId(event.getId());
            eventDTO.setLocation(event.getLocation());
            eventDTO.setDescription(event.getDescription());
            eventDTO.setOccurredAt(event.getOccurredAt());
            return eventDTO;
        }).collect(Collectors.toList()));
        return dto;
    }
}
