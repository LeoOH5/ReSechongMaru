package com.example.SechongMaru.mainpage.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class MainPageResponseDto {

    @Getter
    @Builder
    public static class MonthMeta {
        /** ISO yyyy-MM (예: 2025-08) */
        private String yearMonth;
        /** 그 달의 1일 (yyyy-MM-dd) */
        private String monthStart;
        /** 그 달의 말일 (yyyy-MM-dd) */
        private String monthEnd;
    }

    /** 달력 렌더링에 필요한 최소 메타 (비회원도 내려줌) */
    private MonthMeta month;

    /**
     * 이번 달 주요 정책 = "사용자가 즐겨찾기한 정책" 중,
     * 이 달 기간과 겹치는 정책들. (비회원이면 빈 배열)
     */
    private List<MainPagePolicyCardDto> items;
}
