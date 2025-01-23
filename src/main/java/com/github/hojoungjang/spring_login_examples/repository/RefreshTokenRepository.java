package com.github.hojoungjang.spring_login_examples.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.hojoungjang.spring_login_examples.domain.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByUserId(Long userId);
    Optional<RefreshToken> findByRefreshToken(String refreshToken);
}
