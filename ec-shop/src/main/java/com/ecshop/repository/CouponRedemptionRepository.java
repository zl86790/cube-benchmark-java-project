package com.ecshop.repository;

import com.ecshop.model.CouponRedemption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CouponRedemptionRepository extends JpaRepository<CouponRedemption, Long> {
    List<CouponRedemption> findByUserId(Long userId);

    List<CouponRedemption> findByCouponId(Long couponId);

    long countByCouponIdAndUserId(Long couponId, Long userId);
}
