package com.example.SechongMaru.dto.saved;

import java.time.OffsetDateTime;

public record SavedPolicyResponseDto(
        Long id,
        Long userId,
        Long policyId,
        String status,
        OffsetDateTime savedAt,
        OffsetDateTime updatedAt
) {}