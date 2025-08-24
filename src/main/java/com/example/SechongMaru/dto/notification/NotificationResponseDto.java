package com.example.SechongMaru.dto.notification;

import com.example.SechongMaru.globals.enums.NotificationType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record NotificationResponseDto(
        UUID id,
        UUID userId,
        UUID policyId,
        NotificationType type,
        OffsetDateTime scheduled_for,
        OffsetDateTime sentAt,
        boolean isRead,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}