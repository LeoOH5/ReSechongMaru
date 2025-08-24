package com.example.SechongMaru.dto.policy;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;
import java.util.List;

public record PolicyCreateRequestDto(
        String cityName,
        @NotBlank String title,
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
        List<PolicyRequiredDocResponseDto> requiredDocsKv
) {}