package com.ecshop.service;

import com.ecshop.exception.BusinessException;
import com.ecshop.model.Supplier;
import com.ecshop.repository.SupplierRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupplierService {
    private final SupplierRepository supplierRepository;

    public Supplier getSupplier(Long id) {
        return supplierRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Supplier not found: " + id));
    }

    public List<Supplier> getActiveSuppliers() {
        return supplierRepository.findByIsActiveTrue();
    }

    public Supplier createSupplier(Supplier supplier) {
        return supplierRepository.save(supplier);
    }
}
