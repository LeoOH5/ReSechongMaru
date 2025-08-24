package com.example.SechongMaru.globals.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ✅ 비회원 허용 엔드포인트
                        .requestMatchers(HttpMethod.GET, "/api/main").permitAll()
                        .requestMatchers("/ping", "/actuator/**").permitAll()
                        // 나머지는 우선 전부 허용해 두고 필요 시 나중에 잠그자
                        .anyRequest().permitAll()
                )
                // 기본 로그인폼/HTTP Basic 비활성화(원하지 않으면 제거)
                .httpBasic(hb -> hb.disable())
                .formLogin(fl -> fl.disable());

        return http.build();
    }
}
