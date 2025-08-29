package com.example.SechongMaru.globals.security;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

@Configuration
@Order(1) // 기본(카카오) 체인보다 먼저 적용
public class CrawlingSecurityConfig {

    /**
     * 이 체인은 /api/crawling/** 와 모든 Actuator 엔드포인트만 담당
     * (나머지는 기본 시큐리티 체인이 처리)
     */
    @Bean
    public SecurityFilterChain crawlingFilterChain(
            HttpSecurity http,
            HandlerMappingIntrospector introspector // MvcRequestMatcher에 필요
    ) throws Exception {

        // MVC 패턴 매처 빌더
        MvcRequestMatcher.Builder mvc = new MvcRequestMatcher.Builder(introspector);

        // 체인이 적용될 "대상"을 지정: /api/crawling/** 또는 Actuator(any)
        http.securityMatcher(request ->
                mvc.pattern("/api/crawling/**").matches(request) ||
                        EndpointRequest.toAnyEndpoint().matches(request)
        );

        http
                // 이 체인에 매칭되는 요청은 모두 허용
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(mvc.pattern("/api/crawling/**")).permitAll()
                        .requestMatchers(EndpointRequest.toAnyEndpoint()).permitAll()
                        .anyRequest().denyAll() // 혹시 섞여 들어오면 차단(안전)
                )
                // CSRF는 크롤링/액추에이터에서만 제외
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(mvc.pattern("/api/crawling/**"))
                        .ignoringRequestMatchers(EndpointRequest.toAnyEndpoint())
                );

        return http.build();
    }
}
