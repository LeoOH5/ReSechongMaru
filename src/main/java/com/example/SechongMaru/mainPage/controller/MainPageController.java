package com.example.SechongMaru.mainPage.controller;

import com.example.SechongMaru.mainPage.dto.MainPageResponseDto;
import com.example.SechongMaru.mainPage.service.MainPageService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;

@RestController
@RequestMapping("/api/main")
public class MainPageController {

    private final MainPageService mainPageService;

    public MainPageController(MainPageService mainPageService) {
        this.mainPageService = mainPageService;
    }

    @GetMapping
    public MainPageResponseDto getMainPage(
            @RequestHeader(value = "Authorization", required = false) String authorization,
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        // date가 없으면 Asia/Seoul 기준 오늘 날짜로
        LocalDate baseDate = (date != null) ? date : LocalDate.now(ZoneId.of("Asia/Seoul"));
        return mainPageService.buildMainPage(authorization, baseDate);
    }
}
