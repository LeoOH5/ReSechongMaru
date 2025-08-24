package com.example.SechongMaru.dto.policy;

import java.util.UUID;

public record PolicyRequiredDocResponseDto(
        UUID id,
        String docKey,
        String docUrl
) {}