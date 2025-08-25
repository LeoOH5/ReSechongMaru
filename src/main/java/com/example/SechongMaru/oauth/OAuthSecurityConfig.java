package com.example.SechongMaru.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class OAuthSecurityConfig {

    private final KakaoLogin kakaoLogin;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/health",
                                "/index.html", "/login-test.html",
                                "/public/**", "/css/**", "/js/**",
                                "/authorize",
                                "/api/authorize",// 시작점
                                "/oauth2/**",                // OAuth2 시작
                                "/login/oauth2/**",          // 콜백
                                "/error"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(u -> u.userService(kakaoLogin))
                        .defaultSuccessUrl("/index.html?login=success", true)
                )
                .logout(l -> l.disable()); // 기본 /logout 비활성화

        return http.build();
    }
}
