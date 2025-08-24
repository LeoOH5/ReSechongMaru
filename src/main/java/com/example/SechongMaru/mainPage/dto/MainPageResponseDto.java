package com.example.SechongMaru.mainPage.dto;

import java.util.List;

public record MainPageResponseDto(
        String serverTime,
        String baseDate,
        MainPageRangeDto range,
        List<MainPageEventDto> events,
        MainPageMetricsDto metrics
) {}
