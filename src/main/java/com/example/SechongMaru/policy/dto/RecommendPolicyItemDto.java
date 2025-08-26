package com.example.SechongMaru.policy.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Builder
@Getter
public class RecommendPolicyItemDto {
    private Long policyId;
    private String title;
    private String applyStart; // String으로 변경
    private String applyEnd;   // String으로 변경
    private boolean isScraped;
    private boolean recommend;
}
