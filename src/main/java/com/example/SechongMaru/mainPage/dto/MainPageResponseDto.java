package com.example.SechongMaru.mainpage.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
@Builder
public class MainPageResponseDto {

    @Getter
    @Builder
    public static class Range {
        private String startDate;
        private String endDate;
        private String view;
        private String timezone;
    }

    @Getter
    @Builder
    public static class Event {
        private Long policyId;
        private String title;
        private String applyStart;
        private String applyEnd;
        private Boolean isScraped;
    }

    @Getter
    @Builder
    public static class Metrics {
        private Integer countTotal;
    }

    /** 서버 시간 */
    private String serverTime;
    
    /** 기준 날짜 */
    private String baseDate;
    
    /** 날짜 범위 정보 */
    private Range range;
    
    /** 이벤트 목록 (정책들) */
    private List<Event> events;
    
    /** 메트릭 정보 */
    private Metrics metrics;
}
