package com.example.SechongMaru.mainPage.service;

import com.example.SechongMaru.entity.policy.Policy;
import com.example.SechongMaru.entity.policy.SavedPolicy;
import com.example.SechongMaru.mainPage.dto.*;
import com.example.SechongMaru.mainPage.repository.MainPageSavedPolicyRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MainPageService {

    private static final ZoneId ZONE_SEOUL = ZoneId.of("Asia/Seoul");
    private static final DateTimeFormatter TS_FMT   = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ISO_DATE;

    private final MainPageSavedPolicyRepository savedPolicyRepository;

    public MainPageService(MainPageSavedPolicyRepository savedPolicyRepository) {
        this.savedPolicyRepository = savedPolicyRepository;
    }

    public MainPageResponseDto buildMainPage(String authorizationHeader, LocalDate baseDate) {
        LocalDate start = baseDate.withDayOfMonth(1);
        LocalDate end   = start.plusMonths(1).minusDays(1);

        Optional<UUID> userIdOpt = resolveUserId(authorizationHeader);

        List<MainPageEventDto> events = userIdOpt
                .map(userId ->
                        savedPolicyRepository.findSavedEventsOverlappingMonth(userId, start, end)
                                .stream()
                                .map(this::toEventDto)
                                .collect(Collectors.toList())
                )
                .orElseGet(List::of);

        MainPageMetricsDto metrics = new MainPageMetricsDto(events.size());

        String serverTime = ZonedDateTime.now(ZONE_SEOUL).format(TS_FMT);
        MainPageRangeDto range = new MainPageRangeDto(
                start.format(DATE_FMT),
                end.format(DATE_FMT),
                "month",
                ZONE_SEOUL.getId()
        );

        return new MainPageResponseDto(
                serverTime,
                baseDate.format(DATE_FMT),
                range,
                events,
                metrics
        );
    }

    private MainPageEventDto toEventDto(SavedPolicy sp) {
        Policy p = sp.getPolicy();
        return new MainPageEventDto(
                p.getId(),
                p.getTitle(),
                p.getApplyStart() != null ? p.getApplyStart().format(DATE_FMT) : null,
                p.getApplyEnd()   != null ? p.getApplyEnd().format(DATE_FMT)   : null,
                true
        );
    }

    private Optional<UUID> resolveUserId(String authorizationHeader) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getName() != null) {
            try {
                return Optional.of(UUID.fromString(auth.getName()));
            } catch (IllegalArgumentException ignored) { }
        }
        return Optional.empty();
    }
}
