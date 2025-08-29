// src/main/java/com/example/SechongMaru/policy/controller/PolicyScrapController.java
package com.example.SechongMaru.policy.controller;

import com.example.SechongMaru.policy.dto.ScrapListResponseDto;
import com.example.SechongMaru.policy.service.PolicyScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/policy")
@RequiredArgsConstructor
public class PolicyScrapController {

    private final PolicyScrapService policyScrapService;

    /** 스크랩 목록 조회 */
    @GetMapping("/scrap")
    public ResponseEntity<?> getScraps(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size,
                                       @AuthenticationPrincipal OAuth2User principal) {
        long userId = requireLogin(principal);
        ScrapListResponseDto body = policyScrapService.getMyScraps(userId, page, size);
        return ResponseEntity.ok(body);
    }

    /** 스크랩 저장 */
    @PostMapping("/addscrap/{policyId}")
    public ResponseEntity<?> addScrap(@PathVariable Long policyId,
                                      @AuthenticationPrincipal OAuth2User principal) {
        long userId = requireLogin(principal);
        policyScrapService.addScrap(userId, policyId);
        return ResponseEntity.ok(Map.of("success", true, "policyId", policyId));
    }

    /** 스크랩 삭제 */
    @DeleteMapping("/deletescrap/{policyId}")
    public ResponseEntity<?> deleteScrap(@PathVariable Long policyId,
                                         @AuthenticationPrincipal OAuth2User principal) {
        long userId = requireLogin(principal);
        policyScrapService.deleteScrap(userId, policyId);
        return ResponseEntity.ok(Map.of("success", true, "deletedPolicyId", policyId));
    }

    /** Security Principal에서 카카오 id 추출 (KakaoLogin에서 name attr key = "id") */
    private long requireLogin(OAuth2User principal) {
        if (principal == null) throw new UnauthorizedException("로그인이 필요합니다.");
        Object id = principal.getAttribute("id");
        if (id instanceof Number n) return n.longValue();
        if (id instanceof String s && !s.isBlank()) return Long.parseLong(s);
        throw new UnauthorizedException("카카오 사용자 id를 찾을 수 없습니다.");
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String msg) { super(msg); }
    }
}
