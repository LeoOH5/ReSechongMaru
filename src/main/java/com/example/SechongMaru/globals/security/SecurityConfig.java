package com.example.SechongMaru.globals.security;

import com.example.SechongMaru.oauth.KakaoLogin;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final KakaoLogin kakaoLogin; // Kakao OAuth2UserService (attrs에 "id" 포함)

    /** CORS 전역 설정 (프리플라이트 OPTIONS 허용 + 쿠키 허용) */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowCredentials(true);
        // 개발 편의를 위해 localhost/127.0.0.1 전체 허용 (운영에서는 도메인 고정 권장)
        cfg.setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:*"));
        cfg.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        cfg.setAllowedHeaders(List.of("Authorization","Content-Type","X-Requested-With","Cookie","Accept","Origin"));
        cfg.setExposedHeaders(List.of("Set-Cookie"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", cfg);
        return source;
    }

    /** 단일 SecurityFilterChain: OAuth2 로그인 + 세션 인증 + /api/** 401 정책 */
    @Bean
    @Order(0)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})                 // CORS 활성화
                .formLogin(f -> f.disable())
                .httpBasic(b -> b.disable())

                .authorizeHttpRequests(auth -> auth
                        // 브라우저 프리플라이트 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // 정적/시작점/콜백은 모두 허용
                        .requestMatchers(
                                "/", "/health",
                                "/index.html", "/login-test.html",
                                "/public/**", "/css/**", "/js/**",
                                "/authorize", "/api/authorize",
                                "/oauth2/**", "/login/oauth2/**",
                                "/error"
                        ).permitAll()

                        // 검색 API는 비로그인 허용
                        .requestMatchers(HttpMethod.GET, "/api/search", "/api/search/**").permitAll()

                        // 그 외는 인증 필요 (예: /api/my, /api/myinfo 등)
                        .anyRequest().authenticated()
                )

                // OAuth2 로그인: 성공 후 index.html?login=success
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(u -> u.userService(kakaoLogin)) // KakaoLogin이 attrs에 "id"를 채워줌
                        .defaultSuccessUrl("/index.html?login=success", true)
                )

                // 기본 /logout 은 사용하지 않음 (카카오 로그아웃/연결해제는 컨트롤러/서비스에서 처리)
                .logout(l -> l.disable())

                // /api/** 인증 실패 시 리다이렉트 대신 401 반환 (단, OPTIONS는 제외)
                .exceptionHandling(ex -> ex
                        .defaultAuthenticationEntryPointFor(
                                new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                request -> {
                                    String uri = request.getRequestURI();
                                    String method = request.getMethod();
                                    return uri != null
                                            && uri.startsWith("/api/")
                                            && !"OPTIONS".equalsIgnoreCase(method);
                                }
                        )
                );

        return http.build();
    }
}
