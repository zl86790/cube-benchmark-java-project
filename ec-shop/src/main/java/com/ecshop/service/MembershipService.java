package com.ecshop.service;

import com.ecshop.exception.BusinessException;
import com.ecshop.model.MembershipTier;
import com.ecshop.model.User;
import com.ecshop.model.UserMembership;
import com.ecshop.repository.MembershipTierRepository;
import com.ecshop.repository.UserMembershipRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MembershipService {

    private final MembershipTierRepository membershipTierRepository;
    private final UserMembershipRepository userMembershipRepository;

    public List<MembershipTier> getTiers() {
        return membershipTierRepository.findAllByOrderByMinSpendAsc();
    }

    public MembershipTier createTier(MembershipTier tier) {
        return membershipTierRepository.save(tier);
    }

    public UserMembership getUserMembership(Long userId) {
        return userMembershipRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException("No membership found for user: " + userId));
    }

    @Transactional
    public UserMembership evaluateTier(Long userId, BigDecimal totalSpend) {
        List<MembershipTier> tiers = getTiers();
        MembershipTier eligibleTier = tiers.stream()
                .filter(t -> totalSpend.compareTo(t.getMinSpend()) >= 0)
                .reduce((first, second) -> second)
                .orElseThrow(() -> new BusinessException("No membership tier available"));

        UserMembership membership = userMembershipRepository.findByUserId(userId)
                .orElseGet(() -> {
                    UserMembership newMembership = new UserMembership();
                    User user = new User();
                    user.setId(userId);
                    newMembership.setUser(user);
                    return newMembership;
                });
        membership.setTier(eligibleTier);
        return userMembershipRepository.save(membership);
    }
}
