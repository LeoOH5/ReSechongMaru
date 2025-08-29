package com.example.SechongMaru.dto.policy;

import com.example.SechongMaru.globals.enums.EligibilityAttribute;
import com.example.SechongMaru.globals.enums.EligibilityOperator;

import java.math.BigDecimal;

public record PolicyEligibilityRuleResponseDto(
        Long id,
        EligibilityAttribute attribute,
        EligibilityOperator operator,
        String valueText,
        BigDecimal minValue,
        BigDecimal maxValue,
        Long refInterestId
) {}