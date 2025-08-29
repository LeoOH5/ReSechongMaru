package com.example.SechongMaru.policy.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record RecommendPoliciesResponseDto(
        Paging paging,
        List<RecommendPolicyItemDto> items
) {
    @Builder
    public record Paging(
            int page,
            int size,
            long total,
            boolean has_next
    ) {}
}
