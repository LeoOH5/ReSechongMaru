package com.example.SechongMaru.dto.user;

import com.example.SechongMaru.globals.enums.EmploymentStatus;
import com.example.SechongMaru.globals.enums.HouseholdType;
import com.example.SechongMaru.globals.enums.IncomeType;

import java.math.BigDecimal;

public record UserUpdateProfileRequestDto(
        String cityName,
        EmploymentStatus employStatus,
        IncomeType incomeType,
        BigDecimal monthIncomeMin,
        BigDecimal monthIncomeMax,
        HouseholdType householdType
) {}