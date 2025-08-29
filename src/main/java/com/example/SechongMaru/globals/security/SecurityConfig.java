package com.example.SechongMaru.globals.security;

import com.example.SechongMaru.oauth.KakaoLogin;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final KakaoLogin kakaoLogin;

    @Bean
    public DevAuthFilter devAuthFilter() {
        return new DevAuthFilter();
    }

    @Bean
    @Order(0)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .formLogin(f -> f.disable())
                .httpBasic(b -> b.disable())
                // 익명은 켜둬도 됨. (끄면 헤더 없을 때 401/403 보일 수 있음)
                .addFilterBefore(devAuthFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(reg -> reg
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
