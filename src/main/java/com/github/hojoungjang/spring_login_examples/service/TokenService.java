package com.github.hojoungjang.spring_login_examples.service;

import java.time.Duration;

import org.springframework.stereotype.Service;

import com.github.hojoungjang.spring_login_examples.config.jwt.TokenProvider;
import com.github.hojoungjang.spring_login_examples.domain.User;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class TokenService {
    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    public String createNewAccessToken(String refreshToken) {
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Unexpected token");
        }

        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        User user = userService.findById(userId);

        return tokenProvider.generateToken(user, Duration.ofHours(2));
    }
}
