package com.example.SechongMaru.mainpage.controller;

import com.example.SechongMaru.mainpage.dto.MainPageResponseDto;
import com.example.SechongMaru.mainpage.service.MainPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MainPageController {

    private final MainPageService mainPageService;

    /**
     * 메인 화면 진입 API
     * - 비회원: 빈 items (달력 메타만)
     * - 회원: 이번 달과 겹치는 "즐겨찾기한 정책"을 카드로 반환
     *
     * GET /api/main?year=2025&month=8
     */
    @GetMapping("/main")
    public ResponseEntity<MainPageResponseDto> getMain(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month
    ) {
        Optional<UUID> userIdOpt = resolveUserId();
        UUID userId = userIdOpt.orElse(null);

        MainPageResponseDto dto = mainPageService.getMain(userId, year, month);
        return ResponseEntity.ok(dto);
    }

    private Optional<UUID> resolveUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) return Optional.empty();
        try {
            return Optional.of(UUID.fromString(auth.getPrincipal().toString()));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }
}
