package com.ecshop.repository;

import com.ecshop.model.ShippingCarrier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ShippingCarrierRepository extends JpaRepository<ShippingCarrier, Long> {
    List<ShippingCarrier> findByIsActiveTrue();
}
