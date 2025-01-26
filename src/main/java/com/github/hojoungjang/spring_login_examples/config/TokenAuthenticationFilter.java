package com.github.hojoungjang.spring_login_examples.config;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.github.hojoungjang.spring_login_examples.config.jwt.TokenProvider;
import com.github.hojoungjang.spring_login_examples.config.oauth.OAuth2SuccessHandler;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    // private final static String HEADER_AUTHORIZATION = "Authorization";
    // private final static String TOKEN_PREFIX = "Bearer";

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        // String authorizationHeader = request.getHeader(HEADER_AUTHORIZATION);
        // String token = getAccessToken(authorizationHeader);
        String token = Arrays.stream(request.getCookies())
            .filter(cookie -> OAuth2SuccessHandler.ACCESS_TOKEN_COOKIE_NAME.equals(cookie.getName()))
            .map(Cookie::getValue)
            .findFirst()
            .orElse(null);
        
        if (token != null && tokenProvider.validateToken(token)) {
            Authentication authentication = tokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    // private String getAccessToken(String authorizationHeader) {
    //     if (authorizationHeader != null && authorizationHeader.startsWith(TOKEN_PREFIX)) {
    //         return authorizationHeader.substring(TOKEN_PREFIX.length());
    //     }
    //     return null;
    // }
}
