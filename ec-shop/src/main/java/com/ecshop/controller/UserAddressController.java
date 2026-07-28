package com.ecshop.controller;

import com.ecshop.dto.ApiResponse;
import com.ecshop.model.UserAddress;
import com.ecshop.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/addresses")
@RequiredArgsConstructor
public class UserAddressController {
    private final UserAddressService userAddressService;

    @GetMapping("/user/{userId}")
    public ApiResponse<List<UserAddress>> getAddresses(@PathVariable Long userId) {
        return ApiResponse.success(userAddressService.getAddresses(userId));
    }

    @PostMapping("/user/{userId}")
    public ApiResponse<UserAddress> addAddress(@PathVariable Long userId, @RequestBody UserAddress address) {
        return ApiResponse.success(userAddressService.addAddress(userId, address));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteAddress(@PathVariable Long id) {
        userAddressService.deleteAddress(id);
        return ApiResponse.success("Address deleted", null);
    }
}
