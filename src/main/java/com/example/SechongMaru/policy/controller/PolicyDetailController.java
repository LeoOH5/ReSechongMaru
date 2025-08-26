package com.example.SechongMaru.policy.controller;

import com.example.SechongMaru.policy.dto.PolicyDetailResponseDto;
import com.example.SechongMaru.policy.service.PolicyDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/policy")
@RequiredArgsConstructor
public class PolicyDetailController {

    private final PolicyDetailService policyDetailService;

    @GetMapping("/detail/{policyId}")
    public ResponseEntity<PolicyDetailResponseDto> getPolicyDetail(@PathVariable Long policyId) {
        Optional<UUID> userId = resolveUserId();
        PolicyDetailResponseDto dto = policyDetailService.getPolicyDetail(policyId, userId);
        return ResponseEntity.ok(dto);
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
}
