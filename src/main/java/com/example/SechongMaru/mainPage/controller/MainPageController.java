// src/main/java/com/example/SechongMaru/mainpage/controller/MainPageController.java
package com.example.SechongMaru.mainpage.controller;

import com.example.SechongMaru.mainpage.dto.MainPageResponseDto;
import com.example.SechongMaru.mainpage.service.MainPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MainPageController {

    private final MainPageService mainPageService;

    /** 메인 화면 진입 (로그인 여부에 따라 즐겨찾기 포함/미포함) */
    @GetMapping("/main")
    public ResponseEntity<MainPageResponseDto> getMain(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            @AuthenticationPrincipal OAuth2User principal
    ) {
        Long userId = getUserId(principal).orElse(null); // 로그인 안 했으면 null
        log.info("[/api/main] year={}, month={}, userId={}", year, month, userId);

        MainPageResponseDto dto = mainPageService.getMain(userId, year, month);
        return ResponseEntity.ok(dto);
    }

    /** Kakao OAuth2 principal에서 id 추출 */
    private Optional<Long> getUserId(OAuth2User principal) {
        if (principal == null) return Optional.empty();
        Object id = principal.getAttribute("id"); // KakaoLogin의 nameAttributeKey가 "id"여야 함
        if (id instanceof Number n) return Optional.of(n.longValue());
        if (id instanceof String s && !s.isBlank()) return Optional.of(Long.parseLong(s));
        return Optional.empty();
    }
}
