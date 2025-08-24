package com.example.SechongMaru.dto.policy;

import com.example.SechongMaru.globals.enums.EligibilityAttribute;
import com.example.SechongMaru.globals.enums.EligibilityOperator;

import java.math.BigDecimal;
import java.util.UUID;

public record PolicyEligibilityRuleResponseDto(
        UUID id,
        EligibilityAttribute attribute,
        EligibilityOperator operator,
        String valueText,
        BigDecimal minValue,
        BigDecimal maxValue,
        UUID refInterestId
) {}