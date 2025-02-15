package com.github.hojoungjang.spring_login_examples.controller;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.hojoungjang.spring_login_examples.config.jwt.JwtFactory;
import com.github.hojoungjang.spring_login_examples.config.jwt.JwtProperties;
import com.github.hojoungjang.spring_login_examples.domain.RefreshToken;
import com.github.hojoungjang.spring_login_examples.domain.User;
import com.github.hojoungjang.spring_login_examples.dto.CreateAccessTokenRequest;
import com.github.hojoungjang.spring_login_examples.repository.RefreshTokenRepository;
import com.github.hojoungjang.spring_login_examples.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TokenApiControllerTest {
    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    JwtProperties jwtProperties;
    @Autowired
    UserRepository userRepo;
    @Autowired
    RefreshTokenRepository refreshTokenRepo;

    @BeforeEach
    public void mockMvcSetup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        userRepo.deleteAll();
    }

    @DisplayName("createNewAccessToken: 새로운 엑세스 토큰을 발급한다.")
    @Test
    public void createNewAccessToken() throws Exception {
        // given
        final String url = "/api/token";
        User testUser = userRepo.save(User.builder()
            .email("user@gmail.com")
            .password("test")
            .build());
        String refreshToken = JwtFactory.builder()
            .claims(Map.of("id", testUser.getId()))
            .build()
            .createToken(jwtProperties);
        refreshTokenRepo.save(new RefreshToken(testUser.getId(), refreshToken));

        CreateAccessTokenRequest request = new CreateAccessTokenRequest();
        request.setRefreshToken(refreshToken);
        final String requestBody = objectMapper.writeValueAsString(request);

        // when
        ResultActions resultActions = mockMvc.perform(post(url)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
            .content(requestBody));

        // then
        resultActions
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.accessToken").isNotEmpty());
    }
}
