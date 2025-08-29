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

    // 이 Bean은 제거하고 SecurityConfig에서 통합하여 관리
    // @Bean
    // public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    //     // ... 기존 코드 ...
    // }
}