package com.ecshop.service;

import com.ecshop.exception.BusinessException;
import com.ecshop.exception.ReturnNotAllowedException;
import com.ecshop.model.RefundRecord;
import com.ecshop.model.ReturnRequest;
import com.ecshop.model.User;
import com.ecshop.repository.RefundRecordRepository;
import com.ecshop.repository.ReturnRequestRepository;
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
public class ReturnService {

    private final ReturnRequestRepository returnRequestRepository;
    private final RefundRecordRepository refundRecordRepository;

    @Transactional
    public ReturnRequest createReturnRequest(Long orderId, Long userId, String reason) {
        ReturnRequest request = new ReturnRequest();
        request.setOrderId(orderId);
        User user = new User();
        user.setId(userId);
        request.setUser(user);
        request.setReason(reason);
        return returnRequestRepository.save(request);
    }

    @Transactional
    public ReturnRequest approve(Long id) {
        ReturnRequest request = getReturnRequest(id);
        if (request.getStatus() != ReturnRequest.ReturnStatus.REQUESTED) {
            throw new ReturnNotAllowedException("Only requested returns can be approved");
        }
        request.setStatus(ReturnRequest.ReturnStatus.APPROVED);
        return returnRequestRepository.save(request);
    }

    @Transactional
    public ReturnRequest reject(Long id) {
        ReturnRequest request = getReturnRequest(id);
        if (request.getStatus() != ReturnRequest.ReturnStatus.REQUESTED) {
            throw new ReturnNotAllowedException("Only requested returns can be rejected");
        }
        request.setStatus(ReturnRequest.ReturnStatus.REJECTED);
        request.setResolvedAt(LocalDateTime.now());
        return returnRequestRepository.save(request);
    }

    @Transactional
    public RefundRecord processRefund(Long returnRequestId, BigDecimal amount) {
        ReturnRequest request = getReturnRequest(returnRequestId);
        if (request.getStatus() != ReturnRequest.ReturnStatus.APPROVED
                && request.getStatus() != ReturnRequest.ReturnStatus.RECEIVED) {
            throw new ReturnNotAllowedException("Return must be approved before refunding");
        }

        RefundRecord refund = new RefundRecord();
        refund.setReturnRequest(request);
        refund.setAmount(amount);
        refund.setStatus(RefundRecord.RefundStatus.COMPLETED);
        refund.setProcessedAt(LocalDateTime.now());

        request.setStatus(ReturnRequest.ReturnStatus.REFUNDED);
        request.setResolvedAt(LocalDateTime.now());
        returnRequestRepository.save(request);

        return refundRecordRepository.save(refund);
    }

    public ReturnRequest getReturnRequest(Long id) {
        return returnRequestRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Return request not found: " + id));
    }

    public List<ReturnRequest> getReturnsByUser(Long userId) {
        return returnRequestRepository.findByUserId(userId);
    }
}
