package com.example.SechongMaru.mainPage.dto;

import java.util.UUID;

public record MainPageEventDto(
        UUID policyId,
        String title,
        String applyStart,
        String applyEnd,
        boolean isScraped
) {}
