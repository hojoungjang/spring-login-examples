package com.github.hojoungjang.spring_login_examples.config;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.github.hojoungjang.spring_login_examples.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.github.hojoungjang.spring_login_examples.config.oauth.OAuth2SuccessHandler;
import com.github.hojoungjang.spring_login_examples.config.oauth.OAuth2UserCustomService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class WebOAuthSecurityConfig {
    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    // 스프링 시큐리티 비활성화
    @Bean
    WebSecurityCustomizer configure() {
        return (web) -> web.ignoring()
            .requestMatchers(toH2Console())
            .requestMatchers("/img/**", "/css/**", "/js/**");
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
        .csrf(csrf -> csrf.disable())
        .httpBasic(httpBasic -> httpBasic.disable())
        .formLogin(formLogin -> formLogin.disable())
        .logout(logout -> logout.disable());

        http.sessionManagement(
            sessionManagement -> sessionManagement.sessionCreationPolicy(
                SessionCreationPolicy.STATELESS
            )
        );

        http.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        http.authorizeRequests(requests -> requests
                .requestMatchers("/api/token").permitAll()
                .requestMatchers("/api/**").authenticated()
                .anyRequest().permitAll());

        http.oauth2Login(login -> login
                .loginPage("/login")
                .authorizationEndpoint()
                .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository)
                .and()
                .successHandler(oAuth2SuccessHandler)   // 인중 성공시 리프레시/엑세스 토큰 발급하는 핸들러
                .userInfoEndpoint()
                .userService(oAuth2UserCustomService)) // DefaultOAuth2UserService 상속하는 서비스 클래스
                .logout(logout -> logout
                    .clearAuthentication(true)
                    .deleteCookies(OAuth2SuccessHandler.ACCESS_TOKEN_COOKIE_NAME)
                    .deleteCookies(OAuth2SuccessHandler.REFRESH_TOKEN_COOKIE_NAME));

        // http.logout(logout -> logout
        //         .logoutSuccessUrl("/login"));


        http.exceptionHandling(handling -> handling
                .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        new AntPathRequestMatcher("/api/**")));

        return http.build();
    }
}
