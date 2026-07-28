package com.ecshop.service;

import com.ecshop.exception.BusinessException;
import com.ecshop.model.User;
import com.ecshop.model.UserAddress;
import com.ecshop.repository.UserAddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserAddressService {

    private final UserAddressRepository userAddressRepository;

    public List<UserAddress> getAddresses(Long userId) {
        return userAddressRepository.findByUserId(userId);
    }

    @Transactional
    public UserAddress addAddress(Long userId, UserAddress address) {
        User user = new User();
        user.setId(userId);
        address.setUser(user);

        if (Boolean.TRUE.equals(address.getIsDefault())) {
            clearDefaults(userId);
        }
        return userAddressRepository.save(address);
    }

    @Transactional
    public void deleteAddress(Long id) {
        UserAddress address = userAddressRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Address not found: " + id));
        userAddressRepository.delete(address);
    }

    private void clearDefaults(Long userId) {
        List<UserAddress> addresses = userAddressRepository.findByUserId(userId);
        for (UserAddress address : addresses) {
            if (Boolean.TRUE.equals(address.getIsDefault())) {
                address.setIsDefault(false);
                userAddressRepository.save(address);
            }
        }
    }
}
