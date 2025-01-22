package com.github.hojoungjang.spring_login_examples.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.github.hojoungjang.spring_login_examples.domain.User;
import com.github.hojoungjang.spring_login_examples.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthUserDetailsService implements UserDetailsService {
    private final UserRepository userRepo;

    @Override
    public User loadUserByUsername(String email) {
        return userRepo.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException(email));
    }
}
