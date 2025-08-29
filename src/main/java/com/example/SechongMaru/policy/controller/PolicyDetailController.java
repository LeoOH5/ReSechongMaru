package com.example.SechongMaru.policy.controller;

import com.example.SechongMaru.policy.dto.PolicyDetailResponseDto;
import com.example.SechongMaru.policy.service.PolicyDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/policy")
@RequiredArgsConstructor
public class PolicyDetailController {

    private final PolicyDetailService policyDetailService;

    @GetMapping("/detail/{policyId}")
    public ResponseEntity<PolicyDetailResponseDto> getPolicyDetail(@PathVariable Long policyId) {
        Optional<Long> userId = resolveUserId();
        PolicyDetailResponseDto dto = policyDetailService.getPolicyDetail(policyId, userId);
        return ResponseEntity.ok(dto);
    }

    private Optional<Long> resolveUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null) return Optional.empty();
        try {
            return Optional.of(Long.valueOf(auth.getPrincipal().toString()));
        } catch (IllegalArgumentException ignored) {
            return Optional.empty();
        }
    }
}
