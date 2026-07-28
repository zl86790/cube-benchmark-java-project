package com.ecshop.service;

import com.ecshop.exception.BusinessException;
import com.ecshop.model.Product;
import com.ecshop.model.PurchaseOrder;
import com.ecshop.model.PurchaseOrderItem;
import com.ecshop.model.Supplier;
import com.ecshop.repository.PurchaseOrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseOrderService {
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final SupplierService supplierService;
    private final ProductService productService;
    private final InventoryService inventoryService;

    @Transactional
    public PurchaseOrder createPurchaseOrder(Long supplierId) {
        List<Supplier> supplier = supplierService.getSupplier(supplierId);
        PurchaseOrder order = new PurchaseOrder();
        order.setSupplier(supplier);
        return purchaseOrderRepository.save(order);
    }

    @Transactional
    public PurchaseOrder addItem(Long purchaseOrderId, Long productId, Integer quantity, BigDecimal unitCost) {
        PurchaseOrder order = getPurchaseOrder(purchaseOrderId);
        Product product = productService.getProduct(productId);

        PurchaseOrderItem item = new PurchaseOrderItem();
        item.setPurchaseOrder(order);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setUnitCost(unitCost);
        order.getItems().add(item);

        order.setTotalAmount(order.getItems().stream()
                .map(i -> i.getUnitCost().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));

        return purchaseOrderRepository.save(order);
    }

    @Transactional
    public PurchaseOrder submit(Long purchaseOrderId) {
        PurchaseOrder order = getPurchaseOrder(purchaseOrderId);
        order.setStatus(PurchaseOrder.PurchaseOrderStatus.SUBMITTED);
        return purchaseOrderRepository.save(order);
    }

    @Transactional
    public PurchaseOrder receive(Long purchaseOrderId) {
        PurchaseOrder order = getPurchaseOrder(purchaseOrderId);
        if (order.getStatus() != PurchaseOrder.PurchaseOrderStatus.SUBMITTED) {
            throw new BusinessException("Only submitted purchase orders can be received");
        }
        for (PurchaseOrderItem item : order.getItems()) {
            inventoryService.restoreStock(item.getProduct().getId(), item.getQuantity());
        }
        order.setStatus(PurchaseOrder.PurchaseOrderStatus.RECEIVED);
        order.setReceivedAt(LocalDateTime.now());
        return purchaseOrderRepository.save(order);
    }

    public PurchaseOrder getPurchaseOrder(Long id) {
        return purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Purchase order not found: " + id));
    }

    public List<PurchaseOrder> getBySupplier(Long supplierId) {
        return purchaseOrderRepository.findBySupplierId(supplierId);
    }
}
