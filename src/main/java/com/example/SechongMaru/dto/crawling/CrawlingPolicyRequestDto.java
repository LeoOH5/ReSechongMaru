package com.example.SechongMaru.dto.crawling;

import java.util.List;

/** 크롤링된 정책 JSON을 그대로 받는 DTO (느슨한 타입) */
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
        String applyStatus,         // ★ Integer → String (아무 값 허용)
        String applyUrl,            // 문자열 그대로 (URL이든 문장이든)
        String applyStart,          // "YYYY-MM-DD"
        String applyEnd,            // "YYYY-MM-DD"
        String money,
        String duration,            // "6" 같은 개월수 숫자 문자열
        String exclusiveGroup
) {}
