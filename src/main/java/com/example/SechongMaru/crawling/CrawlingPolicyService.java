package com.example.SechongMaru.crawling;

import com.example.SechongMaru.dto.crawling.CrawlingPolicyRequestDto;
import com.example.SechongMaru.entity.policy.Policy;
import com.example.SechongMaru.repository.policy.PolicyRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CrawlingPolicyService {

    private final PolicyRepository policyRepository;

    /** 단건 업서트: PolicyRequiredDoc 사용 안 함 */
    @Transactional
    public Policy upsertOne(CrawlingPolicyRequestDto s) {
        LocalDate start = parseDate(s.applyStart());
        String urlKey = normalize(s.applyUrl());

        Policy p = policyRepository
                .findByTitleAndApplyStartAndApplyUrl(nvl(s.title()), start, urlKey)
                .orElseGet(Policy::new);

        // ───────── 매핑 ─────────
        p.setCityName(s.cityName());
        p.setTitle(nvl(s.title()));
        p.setEmployStatus(s.employStatus());
        p.setMinAge(parseIntOrNull(s.minAge()));
        p.setMaxAge(parseIntOrNull(s.maxAge()));
        p.setSelectionCriteria(s.selectionCriteria());

        // 원문 서류 리스트를 JSON 배열 문자열로 보존 저장
        p.setRequiredDocs(toJsonArrayString(s.requiredDocs()));

        p.setContactInfo(s.contactInfo());
        p.setApplyStatus(mapApplyStatus(s.applyStatus())); // "ONLINE"/"OFFLINE"/null 등 문자열
        p.setApplyUrl(urlKey);
        p.setApplyStart(parseDate(s.applyStart()));
        p.setApplyEnd(parseDate(s.applyEnd()));
        p.setMoney(s.money());
        p.setDuration(parseIntOrNull(s.duration()));
        p.setExclusiveGroup(s.exclusiveGroup());

        // 🔸 정규화 매핑(PolicyRequiredDoc)은 여기서 하지 않음

        return policyRepository.save(p);
    }

    /** 벌크 업서트 */
    @Transactional
    public int upsertBulk(List<CrawlingPolicyRequestDto> list) {
        int n = 0;
        for (CrawlingPolicyRequestDto s : list) {
            upsertOne(s);
            n++;
        }
        return n;
    }

    // ───────── helpers ─────────
    private String mapApplyStatus(Integer v) {
        if (v == null) return null;
        // 팀 규칙에 맞춰 문자열 선택
        return (v == 1) ? "ONLINE" : "OFFLINE";
        // 필요 시 return (v == 1) ? "1" : "0";
    }

    private Integer parseIntOrNull(String s) {
        try { return (s == null || s.isBlank()) ? null : Integer.valueOf(s.trim()); }
        catch (Exception e) { return null; }
    }

    private LocalDate parseDate(String s) {
        try { return (s == null || s.isBlank()) ? null : LocalDate.parse(s); }
        catch (Exception e) { return null; }
    }

    private String normalize(String s) { return (s == null) ? "" : s; }
    private String nvl(String s) { return (s == null) ? "" : s; }

    /** ["A","B"] 형태의 간단 직렬화 (잭슨 사용 가능하면 ObjectMapper로 바꿔도 OK) */
    private String toJsonArrayString(List<String> docs) {
        if (docs == null || docs.isEmpty()) return "[]";
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (String d : docs) {
            if (d == null || d.isBlank()) continue;
            if (!first) sb.append(",");
            sb.append("\"").append(escapeJson(d.trim())).append("\"");
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

    private String escapeJson(String s) {
        // 최소한의 이스케이프만 처리
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
