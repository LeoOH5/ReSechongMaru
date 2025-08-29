package com.example.SechongMaru.policy.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class ScrapItemDto {
    private Long policyId;
    private String title;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate applyStart;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate applyEnd;
}
