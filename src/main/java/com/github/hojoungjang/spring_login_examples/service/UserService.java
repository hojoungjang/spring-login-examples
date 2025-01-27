package com.github.hojoungjang.spring_login_examples.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.github.hojoungjang.spring_login_examples.domain.User;
import com.github.hojoungjang.spring_login_examples.dto.AddUserRequest;
import com.github.hojoungjang.spring_login_examples.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepo;

    public Long save(AddUserRequest request) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return userRepo.save(User.builder()
            .email(request.getEmail())
            .password(encoder.encode(request.getPassword()))
            .build()).getId();
    }

    public User findById(Long userId) {
        return userRepo.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }

    public User findByEmail(String email) {
        return userRepo.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Unexpected user"));
    }
}
