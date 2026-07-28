package com.ecshop.service;

import com.ecshop.exception.GiftCardNotFoundException;
import com.ecshop.exception.InsufficientBalanceException;
import com.ecshop.model.GiftCard;
import com.ecshop.model.GiftCardTransaction;
import com.ecshop.repository.GiftCardRepository;
import com.ecshop.repository.GiftCardTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class GiftCardService {

    private final GiftCardRepository giftCardRepository;
    private final GiftCardTransactionRepository giftCardTransactionRepository;

    @Transactional
    public GiftCard issueGiftCard(BigDecimal amount, LocalDateTime expiresAt) {
        GiftCard giftCard = new GiftCard();
        giftCard.setCode("GC-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase());
        giftCard.setInitialBalance(amount);
        giftCard.setCurrentBalance(amount);
        giftCard.setExpiresAt(expiresAt);
        giftCard = giftCardRepository.save(giftCard);

        recordTransaction(giftCard, GiftCardTransaction.TransactionType.ISSUED, amount);
        return giftCard;
    }

    public GiftCard getGiftCard(String code) {
        return giftCardRepository.findByCode(code)
                .orElseThrow(() -> new GiftCardNotFoundException(code));
    }

    @Transactional
    public GiftCard redeem(String code, BigDecimal amount) {
        GiftCard giftCard = getGiftCard(code);
        if (!giftCard.getIsActive() || giftCard.getCurrentBalance().compareTo(amount) < 0) {
            throw new InsufficientBalanceException(code);
        }
        if (giftCard.getExpiresAt() != null && LocalDateTime.now().isAfter(giftCard.getExpiresAt())) {
            throw new InsufficientBalanceException(code);
        }

        giftCard.setCurrentBalance(giftCard.getCurrentBalance().subtract(amount));
        giftCard = giftCardRepository.save(giftCard);

        recordTransaction(giftCard, GiftCardTransaction.TransactionType.REDEEMED, amount);
        return giftCard;
    }

    private void recordTransaction(GiftCard giftCard, GiftCardTransaction.TransactionType type, BigDecimal amount) {
        GiftCardTransaction transaction = new GiftCardTransaction();
        transaction.setGiftCard(giftCard);
        transaction.setType(type);
        transaction.setAmount(amount);
        giftCardTransactionRepository.save(transaction);
    }
}
