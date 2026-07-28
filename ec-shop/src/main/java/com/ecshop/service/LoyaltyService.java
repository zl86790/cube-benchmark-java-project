package com.ecshop.service;

import com.ecshop.exception.BusinessException;
import com.ecshop.model.LoyaltyAccount;
import com.ecshop.model.PointsTransaction;
import com.ecshop.model.User;
import com.ecshop.repository.LoyaltyAccountRepository;
import com.ecshop.repository.PointsTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoyaltyService {

    private final LoyaltyAccountRepository loyaltyAccountRepository;
    private final PointsTransactionRepository pointsTransactionRepository;

    public LoyaltyAccount getAccount(Long userId) {
        return loyaltyAccountRepository.findByUserId(userId)
                .orElseGet(() -> createAccount(userId));
    }

    private LoyaltyAccount createAccount(Long userId) {
        LoyaltyAccount account = new LoyaltyAccount();
        User user = new User();
        user.setId(userId);
        account.setUser(user);
        return loyaltyAccountRepository.save(account);
    }

    @Transactional
    public LoyaltyAccount earnPoints(Long userId, Long points, String description) {
        LoyaltyAccount account = getAccount(userId);
        account.setPointsBalance(account.getPointsBalance() + points);
        account.setLifetimePoints(account.getLifetimePoints() + points);
        loyaltyAccountRepository.save(account);

        recordTransaction(account, PointsTransaction.TransactionType.EARNED, points, description);
        return account;
    }

    @Transactional
    public LoyaltyAccount redeemPoints(Long userId, Long points, String description) {
        LoyaltyAccount account = getAccount(userId);
        if (account.getPointsBalance() < points) {
            throw new BusinessException("Insufficient points balance");
        }
        account.setPointsBalance(account.getPointsBalance() - points);
        loyaltyAccountRepository.save(account);

        recordTransaction(account, PointsTransaction.TransactionType.REDEEMED, points, description);
        return account;
    }

    private void recordTransaction(LoyaltyAccount account, PointsTransaction.TransactionType type, Long points,
                                    String description) {
        PointsTransaction transaction = new PointsTransaction();
        transaction.setLoyaltyAccount(account);
        transaction.setType(type);
        transaction.setPoints(points);
        transaction.setDescription(description);
        pointsTransactionRepository.save(transaction);
    }

    public List<PointsTransaction> getHistory(Long userId) {
        LoyaltyAccount account = getAccount(userId);
        return pointsTransactionRepository.findByLoyaltyAccountIdOrderByCreatedAtDesc(account.getId());
    }
}
