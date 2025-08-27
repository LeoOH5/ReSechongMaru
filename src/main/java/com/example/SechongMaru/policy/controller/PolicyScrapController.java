package com.example.SechongMaru.policy.controller;

import com.example.SechongMaru.policy.dto.ScrapListResponseDto;
import com.example.SechongMaru.policy.service.PolicyScrapService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/policy")
@RequiredArgsConstructor
public class PolicyScrapController {

    private final PolicyScrapService policyScrapService;

    /** 스크랩 목록 조회 */
    @GetMapping("/scrap")
    public ResponseEntity<?> getScraps(@RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        Optional<UUID> userIdOpt = resolveUserId();
        if (userIdOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiError("unauthorized", "로그인이 필요합니다."));
        }
        ScrapListResponseDto body = policyScrapService.getMyScraps(userIdOpt.get(), page, size);
        return ResponseEntity.ok(body);
    }

    /** 스크랩 저장 */
    @PostMapping("/addscrap/{policyId}")
    public ResponseEntity<?> addScrap(@PathVariable Long policyId) {
        Optional<UUID> userIdOpt = resolveUserId();
        if (userIdOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiError("unauthorized", "로그인이 필요합니다."));
        }
        policyScrapService.addScrap(userIdOpt.get(), policyId);
        return ResponseEntity.ok(Map.of("success", true, "policyId", policyId));
    }

    /** 스크랩 삭제 */
    @DeleteMapping("/deletescrap/{policyId}")
    public ResponseEntity<?> deleteScrap(@PathVariable Long policyId) {
        Optional<UUID> userIdOpt = resolveUserId();
        if (userIdOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiError("unauthorized", "로그인이 필요합니다."));
        }
        policyScrapService.deleteScrap(userIdOpt.get(), policyId);
        return ResponseEntity.ok(Map.of("success", true, "deletedPolicyId", policyId));
    }

    private Optional<UUID> resolveUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) return Optional.empty();
        try {
            return Optional.of(UUID.fromString(auth.getPrincipal().toString()));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }

    private record ApiError(String code, String message) {}
}
