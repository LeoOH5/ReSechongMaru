package com.example.SechongMaru.oauth;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;


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
                                "/api/authorize",   // 시작점
                                "/oauth2/**",       // OAuth2 시작
                                "/login/oauth2/**", // 콜백
                                "/error"
                        ).permitAll()

                        // ✅ 검색 API는 로그인 없이 허용
                        .requestMatchers(HttpMethod.GET, "/api/search", "/api/search/**").permitAll()

                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(u -> u.userService(kakaoLogin))
                        .defaultSuccessUrl("/index.html?login=success", true)
                )
                .logout(l -> l.disable()) // 기본 /logout 비활성화

                // ✅ API 요청 인증 실패 시 로그인 페이지로 리다이렉트하지 말고 401 반환
                .exceptionHandling(ex -> ex
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                request -> request.getRequestURI().startsWith("/api/")
                        )
                );

        return http.build();
    }
}