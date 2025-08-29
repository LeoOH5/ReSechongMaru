package com.example.SechongMaru.mainpage.service;

import com.example.SechongMaru.entity.policy.SavedPolicy;
import com.example.SechongMaru.mainpage.dto.MainPageResponseDto;
import com.example.SechongMaru.repository.policy.SavedPolicyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MainPageService {

    private final SavedPolicyRepository savedRepo;

    public MainPageResponseDto getMain(Long userIdOrNull, Integer yearOrNull, Integer monthOrNull) {
        try {
            log.info("MainPageService.getMain() called - userId: {}, year: {}, month: {}", userIdOrNull, yearOrNull, monthOrNull);

            // 1) 기준 월 계산 (미지정이면 현재 달)
            YearMonth ym = resolveYearMonth(yearOrNull, monthOrNull);
            LocalDate monthStart = ym.atDay(1);
            LocalDate monthEnd = ym.atEndOfMonth();
            
            log.info("Resolved yearMonth: {}, monthStart: {}, monthEnd: {}", ym, monthStart, monthEnd);

            // 2) 현재 시간 정보
            LocalDateTime now = LocalDateTime.now();
            String serverTime = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String baseDate = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            // 3) 비회원이면 빈 배열 + 기본 정보만
            if (userIdOrNull == null) {
                log.info("User is not logged in, returning empty response");
                return MainPageResponseDto.builder()
                        .serverTime(serverTime)
                        .baseDate(baseDate)
                        .range(MainPageResponseDto.Range.builder()
                                .startDate(monthStart.toString())
                                .endDate(monthEnd.toString())
                                .view("month")
                                .timezone("Asia/Seoul")
                                .build())
                        .events(Collections.emptyList())
                        .metrics(MainPageResponseDto.Metrics.builder()
                                .countTotal(0)
                                .build())
                        .build();
            }

            // 4) 회원이면: 이번 달과 기간이 겹치는 "즐겨찾기한 정책"만 조회
            log.info("User is logged in (userId: {}), fetching saved policies", userIdOrNull);
            List<SavedPolicy> savedInMonth = savedRepo.findMonthlySavedPolicies(
                    userIdOrNull, monthStart, monthEnd);
            
            log.info("Found {} saved policies for the month", savedInMonth.size());

            List<MainPageResponseDto.Event> events = savedInMonth.stream()
                    .map(sp -> {
                        var p = sp.getPolicy();
                        return MainPageResponseDto.Event.builder()
                                .policyId(p.getId())
                                .title(p.getTitle())
                                .applyStart(p.getApplyStart() != null ? p.getApplyStart().toString() : null)
                                .applyEnd(p.getApplyEnd() != null ? p.getApplyEnd().toString() : null)
                                .isScraped(true)     // 저장 목록이므로 항상 true
                                .build();
                    }).toList();

            MainPageResponseDto result = MainPageResponseDto.builder()
                    .serverTime(serverTime)
                    .baseDate(baseDate)
                    .range(MainPageResponseDto.Range.builder()
                            .startDate(monthStart.toString())
                            .endDate(monthEnd.toString())
                            .view("month")
                            .timezone("Asia/Seoul")
                            .build())
                    .events(events)
                    .metrics(MainPageResponseDto.Metrics.builder()
                            .countTotal(events.size())
                            .build())
                    .build();
            
            log.info("Successfully built response with {} items", events.size());
            return result;
            
        } catch (Exception e) {
            log.error("Error in MainPageService.getMain()", e);
            throw new RuntimeException("메인 페이지 데이터 조회 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    private YearMonth resolveYearMonth(Integer year, Integer month) {
        if (year == null || month == null) {
            return YearMonth.now();
        }
        // month 범위 방어
        int m = Math.min(Math.max(month, 1), 12);
        return YearMonth.of(year, m);
    }
}
