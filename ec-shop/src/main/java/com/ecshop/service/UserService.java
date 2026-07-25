package com.ecshop.service;

import com.ecshop.dto.UserDTO;
import com.ecshop.exception.BusinessException;
import com.ecshop.model.User;
import com.ecshop.repository.UserRepository;
import com.ecshop.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User register(User user) {
        // BUG #12 (MEDIUM): Email validation regex is too permissive
        // ValidationUtils.isValidEmail allows invalid emails like "user@domain" without TLD
        if (!ValidationUtils.isValidEmail(user.getEmail())) {
            throw new BusinessException("Invalid email format");
        }

        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BusinessException("Username already exists");
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BusinessException("Email already registered");
        }

        // In production, password should be hashed
        return userRepository.save(user);
    }

    public User getUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new BusinessException("User not found with id: " + id));
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new BusinessException("User not found: " + username));
    }

    @Transactional
    public User updateUser(Long id, User updated) {
        User existing = getUser(id);
        existing.setFullName(updated.getFullName());
        existing.setPhone(updated.getPhone());
        return userRepository.save(existing);
    }

    @Transactional
    public void deactivateUser(Long id) {
        User user = getUser(id);
        user.setIsActive(false);
        userRepository.save(user);
    }

    public UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setPhone(user.getPhone());
        dto.setRole(user.getRole().name());
        return dto;
    }

    public List<UserDTO> toDTOList(List<User> users) {
        return users.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
