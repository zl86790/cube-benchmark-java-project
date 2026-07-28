package com.ecshop.service;

import com.ecshop.dto.OrderDTO;
import com.ecshop.dto.OrderItemDTO;
import com.ecshop.model.Cart;
import com.ecshop.model.CartItem;
import com.ecshop.model.Order;
import com.ecshop.model.Order.OrderStatus;
import com.ecshop.model.OrderItem;
import com.ecshop.model.Payment;
import com.ecshop.model.User;
import com.ecshop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartService cartService;
    private final ProductService productService;
    private final InventoryService inventoryService;
    private final PaymentService paymentService;
    private final DiscountService discountService;
    private final TaxService taxService;
    private final ShippingService shippingService;
    private final NotificationService notificationService;

    @Transactional
    public Order createOrder(Long userId, Long addressId, String discountCode, String notes) {
        Cart cart = cartService.getCartByUserId(userId);
        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("购物车为空，无法创建订单");
        }

        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString().substring(0, 10));
        order.setUser(new User()); // Simplified: should fetch real user
        order.setStatus(OrderStatus.PENDING);
        order.setNotes(notes);
        order.setCreatedAt(LocalDateTime.now());

        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.setSubtotal(cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
            orderItems.add(orderItem);
        }
        order.setItems(orderItems);

        calculateOrderTotals(order, discountCode);
        boolean stockDeducted = inventoryService.deductStock(order.getItems());
        if (!stockDeducted) {
            throw new IllegalStateException("库存不足");
        }

        Order savedOrder = orderRepository.save(order);
        paymentService.processPayment(savedOrder, Payment.PaymentMethod.CREDIT_CARD, null);

        cartService.clearCart(userId);
        notificationService.sendOrderConfirmation(savedOrder);

        return savedOrder;
    }

    public Order getOrder(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    public Page<Order> getUserOrders(Long userId, int page, int size) {
        return orderRepository.findByUserId(userId, PageRequest.of(page, size));
    }

    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = getOrder(orderId);
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("订单已取消");
        }
        if (order.getStatus() == OrderStatus.SHIPPED) {
            throw new IllegalStateException("已发货订单无法取消");
        }
        order.setStatus(OrderStatus.CANCELLED);
        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = getOrder(orderId);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    private void calculateOrderTotals(Order order, String discountCode) {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (OrderItem item : order.getItems()) {
            subtotal = subtotal.add(
                    item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()))
            );
        }
        order.setSubtotal(subtotal);

        BigDecimal discount = BigDecimal.ZERO;
        if (discountCode != null && !discountCode.isBlank()) {
            discount = discountService.applyDiscount(discountCode, subtotal);
        }
        order.setDiscountAmount(discount);

        BigDecimal afterDiscount = subtotal.subtract(discount);
        BigDecimal tax = taxService.calculateTax(afterDiscount);
        order.setTaxAmount(tax);

        BigDecimal shipping = shippingService.calculateShippingFee(order);
        order.setShippingFee(shipping);

        BigDecimal total = afterDiscount.add(tax).add(shipping);
        order.setTotalAmount(total);
    }

    public OrderDTO toDTO(Order order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setOrderNumber(order.getOrderNumber());
        dto.setStatus(order.getStatus().name());
        dto.setSubtotal(order.getSubtotal());
        dto.setTaxAmount(order.getTaxAmount());
        dto.setShippingFee(order.getShippingFee());
        dto.setDiscountAmount(order.getDiscountAmount());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setCreatedAt(order.getCreatedAt());

        dto.setItems(order.getItems().stream().map(item -> {
            OrderItemDTO itemDTO = new OrderItemDTO();
            itemDTO.setId(item.getId());
            itemDTO.setProductId(item.getProduct().getId());
            itemDTO.setProductName(item.getProduct().getName());
            itemDTO.setQuantity(item.getQuantity());
            itemDTO.setUnitPrice(item.getUnitPrice());
            itemDTO.setSubtotal(item.getSubtotal());
            itemDTO.setStatus(item.getStatus() != null ? item.getStatus().name() : null);
            return itemDTO;
        }).toList());

        return dto;
    }
}
