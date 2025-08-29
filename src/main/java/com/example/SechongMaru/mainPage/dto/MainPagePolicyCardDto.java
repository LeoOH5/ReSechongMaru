package com.example.SechongMaru.mainpage.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class MainPagePolicyCardDto {
    private Long policyId;
    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate applyStart;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate applyEnd;

    private boolean scraped;   // 즐겨찾기(=항상 true: saved 목록이므로)
    private boolean recommend; // 메인 정책 카드에선 일단 false(추천은 별도 API)
}
