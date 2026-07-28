package com.ecshop.controller;

import com.ecshop.dto.ApiResponse;
import com.ecshop.dto.UserDTO;
import com.ecshop.model.User;
import com.ecshop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ApiResponse<UserDTO> register(@RequestBody User user) {
        User created = userService.register(user);
        return ApiResponse.success(userService.toDTO(created));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserDTO> getUser(@PathVariable Long id) {
        User user = userService.getUser(id);
        return ApiResponse.success(userService.toDTO(user));
    }

    @PutMapping("/{id}")
    public ApiResponse<UserDTO> updateUser(@PathVariable Long id, @RequestBody User user) {
        User updated = userService.updateUser(id, user);
        return ApiResponse.success(userService.toDTO(updated));
    }

    @PostMapping("/{id}/deactivate")
    public ApiResponse<Void> deactivateUser(@PathVariable Long id) {
        userService.deactivateUser(id);
        return ApiResponse.success("User deactivated", null);
    }
}
