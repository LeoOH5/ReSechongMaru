package com.example.SechongMaru.globals.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

        String auth = req.getHeader("Authorization");
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7).trim();  // 공백 제거
            // 형식 검증 없이 principal 로 그대로 사용
            var authentication = new UsernamePasswordAuthenticationToken(token, null, List.of());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("[DevAuth] authenticated principal={}", token);
        } else {
            log.debug("[DevAuth] no Authorization header");
        }
        chain.doFilter(req, res);
    }
}
