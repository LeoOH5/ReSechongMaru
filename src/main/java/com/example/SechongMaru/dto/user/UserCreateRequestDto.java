package com.example.SechongMaru.dto.user;

import com.example.SechongMaru.globals.enums.EmploymentStatus;
import com.example.SechongMaru.globals.enums.HouseholdType;
import com.example.SechongMaru.globals.enums.IncomeType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UserCreateRequestDto(
        @NotBlank String name,
        @Past LocalDate birthDate,
        String cityName,
        EmploymentStatus employStatus,
        IncomeType incomeType,
        @Min(0) BigDecimal monthIncomeMin,
        BigDecimal monthIncomeMax,
        HouseholdType householdType
) {}