// src/main/java/com/example/SechongMaru/policy/controller/PolicyRecommendController.java
package com.example.SechongMaru.policy.controller;

import com.example.SechongMaru.policy.dto.RecommendPoliciesResponseDto;
import com.example.SechongMaru.policy.service.PolicyRecommendService;
import com.example.SechongMaru.repository.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/policy")
public class PolicyRecommendController {

    private static final Logger log = LoggerFactory.getLogger(PolicyRecommendController.class);

    private final PolicyRecommendService recommendService;
    private final UserRepository userRepo;

    public PolicyRecommendController(PolicyRecommendService recommendService, UserRepository userRepo) {
        this.recommendService = recommendService;
        this.userRepo = userRepo;
    }

    @GetMapping("/recommend")
    public ResponseEntity<?> recommend(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @AuthenticationPrincipal OAuth2User principal
    ) {
        try {
            long userId = requireLogin(principal);

            var user = userRepo.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "unauthorized", "message", "user not found"));
            }

            int page0 = Math.max(page - 1, 0);
            int sizeSafe = Math.max(size, 1);
            LocalDate base = (date != null) ? date : LocalDate.now();

            RecommendPoliciesResponseDto dto =
                    recommendService.recommend(user, page0, sizeSafe, base);

            return ResponseEntity.ok(dto);
        } catch (UnauthorizedException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "unauthorized", "message", e.getMessage()));
        } catch (Exception e) {
            log.error("[/api/policy/recommend] failed", e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "internal", "message", e.getMessage()));
        }
    }

    /** Kakao OAuth2 principal에서 id 추출 */
    private long requireLogin(OAuth2User principal) {
        if (principal == null) throw new UnauthorizedException("로그인이 필요합니다.");
        Object id = principal.getAttribute("id");
        if (id instanceof Number n) return n.longValue();
        if (id instanceof String s && !s.isBlank()) return Long.parseLong(s);
        throw new UnauthorizedException("카카오 사용자 id를 찾을 수 없습니다.");
    }

    static class UnauthorizedException extends RuntimeException {
        public UnauthorizedException(String msg) { super(msg); }
    }
}
