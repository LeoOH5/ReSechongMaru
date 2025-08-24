package com.example.SechongMaru.dto.saved;

import java.time.OffsetDateTime;
import java.util.UUID;

public record SavedPolicyResponseDto(
        UUID id,
        Long userId,
        UUID policyId,
        String status,
        OffsetDateTime savedAt,
        OffsetDateTime updatedAt
) {}