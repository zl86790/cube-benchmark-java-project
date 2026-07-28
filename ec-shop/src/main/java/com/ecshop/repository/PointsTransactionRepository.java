package com.ecshop.repository;

import com.ecshop.model.PointsTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PointsTransactionRepository extends JpaRepository<PointsTransaction, Long> {

    List<PointsTransaction> findByLoyaltyAccountIdOrderByCreatedAtDesc(Long loyaltyAccountId);
}
