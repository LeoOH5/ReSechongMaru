package com.example.SechongMaru.globals.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

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
                .authorizeHttpRequests(reg -> reg.anyRequest().permitAll());
        return http.build();
    }
}
