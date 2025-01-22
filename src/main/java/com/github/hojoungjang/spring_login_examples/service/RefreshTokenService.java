package com.github.hojoungjang.spring_login_examples.service;

import org.springframework.stereotype.Service;

import com.github.hojoungjang.spring_login_examples.domain.RefreshToken;
import com.github.hojoungjang.spring_login_examples.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepo;

    public RefreshToken findByRefreshToken(String refreshToken) {
        return refreshTokenRepo.findByRefreshToken(refreshToken)
            .orElseThrow(() -> new IllegalArgumentException("Unexpected token"));
    }
}
