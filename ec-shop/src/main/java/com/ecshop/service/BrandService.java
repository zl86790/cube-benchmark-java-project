package com.ecshop.service;

import com.ecshop.exception.BusinessException;
import com.ecshop.model.Brand;
import com.ecshop.repository.BrandRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrandService {

    private final BrandRepository brandRepository;

    public Brand getBrand(Long id) {
        return brandRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Brand not found: " + id));
    }

    public List<Brand> getActiveBrands() {
        return brandRepository.findByIsActiveTrue();
    }

    public Brand createBrand(Brand brand) {
        return brandRepository.save(brand);
    }
}
