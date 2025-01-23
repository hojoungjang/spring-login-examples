package com.github.hojoungjang.spring_login_examples.config.jwt;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.github.hojoungjang.spring_login_examples.domain.User;
import com.github.hojoungjang.spring_login_examples.repository.UserRepository;

import io.jsonwebtoken.Jwts;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class TokenProviderTest {
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private JwtProperties jwtProperties;

    @DisplayName("generateToken(): 유저 정보와 만료 기간을 전달해 토큰을 만들 수 있다.")
    @Test
    void generateToken() {
        // given
        User testUser = userRepo.save(User.builder()
            .email("user@gmail.com")
            .password("test")
            .build());
        
        // when
        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));

        // then
        Long userId = Jwts.parser()
            .setSigningKey(jwtProperties.getSecretKey())
            .parseClaimsJws(token)
            .getBody()
            .get("id", Long.class);
        
        assertThat(userId).isEqualTo(testUser.getId());
    }

    @DisplayName("validateToken(): 만료된 토큰인 경우에 유효성 검증에 실패한다.")
    @Test
    void validateToken_invalidateToken() {
        // given
        String token = JwtFactory.builder()
            .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
            .build()
            .createToken(jwtProperties);

        boolean result = tokenProvider.validateToken(token);
        assertThat(result).isFalse();
    }

    @DisplayName("validateToken(): 유효한 토큰인 경우에 유효성 검증에 성공한다.")
    @Test
    void validateToken_validateToken() {
        //given
        String token = JwtFactory.withDefaultValues().createToken(jwtProperties);

        boolean result = tokenProvider.validateToken(token);

        assertThat(result).isTrue();
    }

    @DisplayName("getAuthentication(): 토큰 기반으로 인증정보를 가져올 수 있다.")
    @Test
    void getAuthentication() {
        String userEmail = "user@email.com";
        String token = JwtFactory.builder()
            .subject(userEmail)
            .build()
            .createToken(jwtProperties);

        Authentication authentication = tokenProvider.getAuthentication(token);

        assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(userEmail);
    }

    @DisplayName("getUserId(): 토큰으로 유저 ID를 가져올 수 있다.")
    @Test
    void getUerId() {
        Long userId = 1L;
        String token = JwtFactory.builder()
            .claims(Map.of("id", userId))
            .build()
            .createToken(jwtProperties);

        Long userIdByToken = tokenProvider.getUserId(token);

        assertThat(userIdByToken).isEqualTo(userId);
    }
}
