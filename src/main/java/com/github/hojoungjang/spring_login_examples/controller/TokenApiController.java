package com.github.hojoungjang.spring_login_examples.controller;

import org.springframework.web.bind.annotation.RestController;

import com.github.hojoungjang.spring_login_examples.dto.CreateAccessTokenRequest;
import com.github.hojoungjang.spring_login_examples.dto.CreateAccessTokenResponse;
import com.github.hojoungjang.spring_login_examples.service.TokenService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RequiredArgsConstructor
@RestController
public class TokenApiController {
    private final TokenService tokenService;

    @PostMapping("/api/token")
    public ResponseEntity<CreateAccessTokenResponse> createNewAccessToken(
        @RequestBody CreateAccessTokenRequest request
    ) {
        String newAccessToken = tokenService.createNewAccessToken(request.getRefreshToken());
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(new CreateAccessTokenResponse(newAccessToken));
    }
    
}
