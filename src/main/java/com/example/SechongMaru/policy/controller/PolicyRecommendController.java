// src/main/java/com/example/SechongMaru/policy/controller/PolicyRecommendController.java
package com.example.SechongMaru.policy.controller;

import com.example.SechongMaru.policy.dto.RecommendPoliciesResponseDto;
import com.example.SechongMaru.policy.service.PolicyRecommendService;
import com.example.SechongMaru.repository.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

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
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth == null || auth.getPrincipal() == null) {
                return ResponseEntity.status(401).body(
                        java.util.Map.of("error", "unauthorized", "message", "no principal"));
            }
            Long userId = Long.valueOf(auth.getPrincipal().toString());

            var user = userRepo.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.status(401).body(
                        java.util.Map.of("error", "unauthorized", "message", "user not found"));
            }

            int page0 = Math.max(page - 1, 0);
            int sizeSafe = Math.max(size, 1);
            LocalDate base = (date != null) ? date : LocalDate.now();

            RecommendPoliciesResponseDto dto =
                    recommendService.recommend(user, page0, sizeSafe, base);

            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            log.error("[/api/policy/recommend] failed", e);
            return ResponseEntity.internalServerError().body(
                    java.util.Map.of("error", "internal", "message", e.getMessage()));
        }
    }
}
