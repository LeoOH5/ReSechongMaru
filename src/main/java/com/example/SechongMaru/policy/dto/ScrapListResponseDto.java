package com.example.SechongMaru.policy.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
@Builder
public class ScrapListResponseDto {
    private Map<String, Object> paging; // page, size, total, has_next
    private List<ScrapItemDto> items;
}
