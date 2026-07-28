package com.ecshop.service;

import com.ecshop.exception.WarehouseNotFoundException;
import com.ecshop.model.Product;
import com.ecshop.model.Warehouse;
import com.ecshop.model.WarehouseStock;
import com.ecshop.repository.WarehouseRepository;
import com.ecshop.repository.WarehouseStockRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseService {

    private final WarehouseRepository warehouseRepository;
    private final WarehouseStockRepository warehouseStockRepository;
    private final ProductService productService;

    public Warehouse getWarehouse(Long id) {
        return warehouseRepository.findById(id)
                .orElseThrow(() -> new WarehouseNotFoundException(id));
    }

    public List<Warehouse> getActiveWarehouses() {
        return warehouseRepository.findByIsActiveTrue();
    }

    public Warehouse createWarehouse(Warehouse warehouse) {
        return warehouseRepository.save(warehouse);
    }

    public List<WarehouseStock> getStockByWarehouse(Long warehouseId) {
        return warehouseStockRepository.findByWarehouseId(warehouseId);
    }

    public List<WarehouseStock> getStockByProduct(Long productId) {
        return warehouseStockRepository.findByProductId(productId);
    }

    @Transactional
    public WarehouseStock setStock(Long warehouseId, Long productId, Integer quantity) {
        Warehouse warehouse = getWarehouse(warehouseId);
        Product product = productService.getProduct(productId);

        WarehouseStock stock = warehouseStockRepository.findByWarehouseIdAndProductId(warehouseId, productId)
                .orElseGet(() -> {
                    WarehouseStock newStock = new WarehouseStock();
                    newStock.setWarehouse(warehouse);
                    newStock.setProduct(product);
                    return newStock;
                });
        stock.setQuantity(quantity);
        return warehouseStockRepository.save(stock);
    }
}
