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
    private final BCryptPasswordEncoder passwordEncoder;

    public Long save(AddUserRequest request) {
        return userRepo.save(User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .build()).getId();
            
    }
}
