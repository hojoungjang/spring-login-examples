package com.github.hojoungjang.spring_login_examples.config.oauth;

import java.io.IOException;
import java.time.Duration;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.github.hojoungjang.spring_login_examples.config.jwt.TokenProvider;
import com.github.hojoungjang.spring_login_examples.domain.RefreshToken;
import com.github.hojoungjang.spring_login_examples.domain.User;
import com.github.hojoungjang.spring_login_examples.repository.RefreshTokenRepository;
import com.github.hojoungjang.spring_login_examples.service.UserService;
import com.github.hojoungjang.spring_login_examples.util.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    public static final Duration REFRESH_TOKEN_DURATION = Duration.ofDays(14);
    public static final Duration ACCESS_TOKEN_DURATION = Duration.ofDays(1);
    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    public static final String REDIRECT_PATH = "/";

    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepo;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository authorizationRequestRepo;
    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(
        HttpServletRequest request,
        HttpServletResponse response,
        Authentication authentication
    ) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        User user = userService.findByEmail((String) oAuth2User.getAttributes().get("email"));

        String refreshToken = tokenProvider.generateToken(user, REFRESH_TOKEN_DURATION);
        saveRefreshToken(user.getId(), refreshToken);
        addTokenToCookie(
            request, response, refreshToken,
            REFRESH_TOKEN_COOKIE_NAME,
            (int) REFRESH_TOKEN_DURATION.toSeconds()
        );

        String accessToken = tokenProvider.generateToken(user, ACCESS_TOKEN_DURATION);
        addTokenToCookie(
            request, response, accessToken,
            ACCESS_TOKEN_COOKIE_NAME,
            (int) ACCESS_TOKEN_DURATION.toSeconds()
        );

        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, getRedirectUrl());
    }

    private void saveRefreshToken(Long userId, String newRefreshToken) {
        RefreshToken refreshToken = refreshTokenRepo.findByUserId(userId)
            .map(entity -> entity.update(newRefreshToken))
            .orElse(new RefreshToken(userId, newRefreshToken));

        refreshTokenRepo.save(refreshToken);
    }

    private void addTokenToCookie(
        HttpServletRequest request,
        HttpServletResponse response,
        String token,
        String cookieName,
        int cookieMaxAge
    ) {
        CookieUtil.deleteCookie(request, response, cookieName);
        CookieUtil.addCookie(response, cookieName, token, cookieMaxAge);
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepo.removeAuthorizationRequestCookies(request, response);
    }

    private String getRedirectUrl() {
        return UriComponentsBuilder.fromUriString(REDIRECT_PATH)
            .build()
            .toUriString();
    }
}
