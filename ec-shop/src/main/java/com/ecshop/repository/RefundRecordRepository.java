package com.ecshop.repository;

import com.ecshop.model.RefundRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface RefundRecordRepository extends JpaRepository<RefundRecord, Long> {
    Optional<RefundRecord> findByReturnRequestId(Long returnRequestId);
}
