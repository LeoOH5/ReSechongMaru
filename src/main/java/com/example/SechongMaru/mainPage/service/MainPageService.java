package com.example.SechongMaru.mainpage.service;

import com.example.SechongMaru.entity.policy.SavedPolicy;
import com.example.SechongMaru.mainpage.dto.MainPagePolicyCardDto;
import com.example.SechongMaru.mainpage.dto.MainPageResponseDto;
import com.example.SechongMaru.repository.policy.SavedPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MainPageService {

    private final SavedPolicyRepository savedRepo;

    public MainPageResponseDto getMain(Long userIdOrNull, Integer yearOrNull, Integer monthOrNull) {

        // 1) 기준 월 계산 (미지정이면 현재 달)
        YearMonth ym = resolveYearMonth(yearOrNull, monthOrNull);
        LocalDate monthStart = ym.atDay(1);
        LocalDate monthEnd = ym.atEndOfMonth();

        // 2) 비회원이면 빈 배열 + 달 메타만
        if (userIdOrNull == null) {
            return MainPageResponseDto.builder()
                    .month(MainPageResponseDto.MonthMeta.builder()
                            .yearMonth(ym.toString())
                            .monthStart(monthStart.toString())
                            .monthEnd(monthEnd.toString())
                            .build())
                    .items(Collections.emptyList())
                    .build();
        }

        // 3) 회원이면: 이번 달과 기간이 겹치는 "즐겨찾기한 정책"만 조회
        List<SavedPolicy> savedInMonth = savedRepo.findMonthlySavedPolicies(
                userIdOrNull, monthStart, monthEnd);

        List<MainPagePolicyCardDto> cards = savedInMonth.stream()
                .map(sp -> {
                    var p = sp.getPolicy();
                    return MainPagePolicyCardDto.builder()
                            .policyId(p.getId())
                            .title(p.getTitle())
                            .applyStart(p.getApplyStart())
                            .applyEnd(p.getApplyEnd())
                            .scraped(true)     // 저장 목록이므로 항상 true
                            .recommend(false)   // 추천은 별도 recommend API로 처리
                            .build();
                }).toList();

        return MainPageResponseDto.builder()
                .month(MainPageResponseDto.MonthMeta.builder()
                        .yearMonth(ym.toString())
                        .monthStart(monthStart.toString())
                        .monthEnd(monthEnd.toString())
                        .build())
                .items(cards)
                .build();
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
