package com.example.SechongMaru.policy.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PolicyDetailResponseDto {
    private Long policyId;
    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate applyStart;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate applyEnd;

    private Integer minAge;
    private Integer maxAge;
    private String employStatus;     // 지원대상(문구)

    private String contactInfo;      // 문의처
    private Integer money;           // 지원금 (숫자)
    private Integer duration;        // 기간(개월) (숫자)

    private List<String> requiredDocs; // 문서명 리스트 (URL 매핑은 추후 확장)

    /**
     * 1: 온라인, 0: 방문 (엔티티가 문자열이면 서비스에서 변환)
     */
    private Integer applyStatus;

    /**
     * 온라인이면 URL, 방문이면 안내문구(또는 null)
     */
    private String applyUrl;

    /**
     * 즐겨찾기(저장) 여부
     */
    private boolean isScraped;
}
