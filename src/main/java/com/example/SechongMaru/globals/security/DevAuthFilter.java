package com.example.SechongMaru.globals.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

public class DevAuthFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(DevAuthFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws ServletException, IOException {

        // 1. Authorization 헤더 기반 인증 (기존 Bearer 토큰 방식)
        String auth = req.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7).trim();  // 공백 제거
            // 형식 검증 없이 principal 로 그대로 사용
            var authentication = new UsernamePasswordAuthenticationToken(token, null, List.of());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("[DevAuth] Bearer token authenticated principal={}", token);
            chain.doFilter(req, res);
            return;
        }

        // 2. JSESSIONID 쿠키 기반 인증 (카카오 로그인 세션)
        String jsessionId = extractJsessionId(req);
        if (jsessionId != null && !jsessionId.isEmpty()) {
            // 세션에서 사용자 정보 추출 시도
            HttpSession session = req.getSession(false);
            if (session != null) {
                // 세션에서 사용자 ID나 정보를 가져오려고 시도
                // 개발용으로 간단하게 처리: JSESSIONID의 일부를 사용자 ID로 사용
                String userId = extractUserIdFromJsessionId(jsessionId);
                if (userId != null) {
                    var authentication = new UsernamePasswordAuthenticationToken(userId, null, List.of());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("[DevAuth] JSESSIONID authenticated principal={}, jsessionId={}", userId, jsessionId);
                    chain.doFilter(req, res);
                    return;
                }
            }
            
            // 세션에서 사용자 정보를 가져올 수 없는 경우, 기본값 사용
            var authentication = new UsernamePasswordAuthenticationToken("1", null, List.of());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("[DevAuth] JSESSIONID authenticated with default principal=1, jsessionId={}", jsessionId);
            chain.doFilter(req, res);
            return;
        }

        log.debug("[DevAuth] no Authorization header or JSESSIONID cookie");
        chain.doFilter(req, res);
    }

    private String extractJsessionId(HttpServletRequest request) {
        String cookieHeader = request.getHeader("Cookie");
        if (cookieHeader == null) return null;
        
        String[] cookies = cookieHeader.split(";");
        for (String cookie : cookies) {
            String[] parts = cookie.trim().split("=");
            if (parts.length == 2 && "JSESSIONID".equals(parts[0].trim())) {
                return parts[1].trim();
            }
        }
        return null;
    }

    private String extractUserIdFromJsessionId(String jsessionId) {
        // JSESSIONID에서 사용자 ID를 추출하는 로직
        // 실제로는 세션에서 사용자 정보를 가져와야 하지만, 개발용으로 간단하게 처리
        // JSESSIONID가 있으면 기본적으로 사용자 ID 1을 반환
        return "1";
    }
}
