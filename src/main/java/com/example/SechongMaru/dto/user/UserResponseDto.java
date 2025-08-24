package com.example.SechongMaru.dto.user;

import com.example.SechongMaru.globals.enums.EmploymentStatus;
import com.example.SechongMaru.globals.enums.HouseholdType;
import com.example.SechongMaru.globals.enums.IncomeType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record UserResponseDto(
        UUID id,
        String name,
        LocalDate birthDate,
        String cityName,
        EmploymentStatus employStatus,
        IncomeType incomeType,
        BigDecimal monthIncomeMin,
        BigDecimal monthIncomeMax,
        HouseholdType householdType,
        OffsetDateTime connectedAt,
        List<String> interests,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}