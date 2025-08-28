// src/main/java/com/example/SechongMaru/dto/user/UserResponseDto.java
package com.example.SechongMaru.dto.user;

import com.example.SechongMaru.globals.enums.EmploymentStatus;
import com.example.SechongMaru.globals.enums.HouseholdType;
import com.example.SechongMaru.globals.enums.IncomeType;
import com.fasterxml.jackson.annotation.JsonFormat;   // ⬅️ 추가

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public record UserResponseDto(
        Long id,
        String name,
        LocalDate birthDate,
        String cityName,
        EmploymentStatus employStatus,
        IncomeType incomeType,
        BigDecimal monthIncomeMin,
        BigDecimal monthIncomeMax,
        HouseholdType householdType,

        // ⬇️ 원하는 형식으로 출력 (예: "2025-08-28 17:19:34")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        OffsetDateTime connectedAt,

        List<String> interests,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        OffsetDateTime createdAt,

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        OffsetDateTime updatedAt
) {}
