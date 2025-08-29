package com.example.SechongMaru.mainpage.controller;

import com.example.SechongMaru.mainpage.dto.MainPageResponseDto;
import com.example.SechongMaru.mainpage.service.MainPageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
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
        try {
            log.info("MainPageController.getMain() called - year: {}, month: {}", year, month);
            
            Optional<Long> userIdOpt = resolveUserId();
            Long userId = userIdOpt.orElse(null);
            
            log.info("Resolved userId: {}", userId);

            MainPageResponseDto dto = mainPageService.getMain(userId, year, month);
            log.info("Successfully retrieved MainPageResponseDto: {}", dto);
            
            return ResponseEntity.ok(dto);
            
        } catch (Exception e) {
            log.error("Error in MainPageController.getMain()", e);
            throw e; // 예외를 다시 던져서 Spring Boot의 기본 에러 핸들러가 처리하도록 함
        }
    }

    private Optional<Long> resolveUserId() {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null) {
                log.debug("No authentication found");
                return Optional.empty();
            }
            
            Long userId = Long.valueOf(auth.getPrincipal().toString());
            log.debug("Resolved userId from authentication: {}", userId);
            return Optional.of(userId);
            
        } catch (IllegalArgumentException e) {
            log.warn("Failed to parse userId from authentication principal: {}", e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Unexpected error in resolveUserId()", e);
            return Optional.empty();
        }
    }
}
