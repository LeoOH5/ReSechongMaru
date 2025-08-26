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
        String urlKey = nullIfBlank(s.applyUrl()); // ★ 빈문자→null 통일

        // ★ 조회 키에도 동일 규칙을 적용해야 upsert가 정확히 동작
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

        // ★ 아무 값이나 허용: 그대로 저장
        p.setApplyStatus(s.applyStatus());

        // ★ URL이든 안내문이든 그대로 저장(요구사항)하되, 빈문자면 null
        p.setApplyUrl(urlKey);

        p.setApplyStart(parseDate(s.applyStart()));
        p.setApplyEnd(parseDate(s.applyEnd()));

        p.setMoney(s.money());
        p.setDuration(parseIntOrNull(s.duration()));
        p.setExclusiveGroup(s.exclusiveGroup());

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
    private Integer parseIntOrNull(String s) {
        try { return (s == null || s.isBlank()) ? null : Integer.valueOf(s.trim()); }
        catch (Exception e) { return null; }
    }

    private LocalDate parseDate(String s) {
        try { return (s == null || s.isBlank()) ? null : LocalDate.parse(s.trim()); }
        catch (Exception e) { return null; }
    }

    private String nullIfBlank(String s) { return (s == null || s.isBlank()) ? null : s.trim(); }
    private String nvl(String s) { return (s == null) ? "" : s; }

    /** ["A","B"] 형태의 간단 직렬화 */
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
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
