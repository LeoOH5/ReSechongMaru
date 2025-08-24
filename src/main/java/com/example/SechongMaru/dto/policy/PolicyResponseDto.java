package com.example.SechongMaru.dto.policy;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record PolicyResponseDto(
        UUID id,
        String cityName,
        String title,
        String employStatus,
        Integer minAge,
        Integer maxAge,
        String selectionCriteria,
        String requiredDocs,
        String contactInfo,
        String applyStatus,
        String applyUrl,
        LocalDate applyStart,
        LocalDate applyEnd,
        String money,
        Integer duration,
        String exclusiveGroup,
        List<PolicyEligibilityRuleResponseDto> eligibilityRules,
        List<PolicyRequiredDocResponseDto> requiredDocsKv,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}