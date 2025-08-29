package com.example.SechongMaru.dto.notification;

import com.example.SechongMaru.globals.enums.NotificationType;

import java.time.OffsetDateTime;

public record NotificationResponseDto(
        Long id,
        Long userId,
        Long policyId
) {}