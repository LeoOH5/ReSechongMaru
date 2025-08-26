package com.example.SechongMaru.dto.crawling;

import java.util.List;

/** 크롤링된 정책 JSON을 그대로 받는 DTO */
public record CrawlingPolicyRequestDto(
        String policyId,            // 항상 null로 옴. 무시 가능
        String cityName,
        String title,
        String employStatus,
        String minAge,              // "19" 같은 숫자 문자열
        String maxAge,              // "
        String selectionCriteria,
        List<String> requiredDocs,  // ["임대차계약서", ...]
        String contactInfo,
        Integer applyStatus,        // 1=온라인, 0=방문, null=불명확
        String applyUrl,
        String applyStart,          // "YYYY-MM-DD"
        String applyEnd,            // "YYYY-MM-DD"
        String money,
        String duration,            // "6" 같은 개월수 숫자 문자열
        String exclusiveGroup
) {}
