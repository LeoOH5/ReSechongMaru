package com.example.SechongMaru.policy.service;

import com.example.SechongMaru.entity.policy.Policy;
import com.example.SechongMaru.policy.dto.PolicyDetailResponseDto;
import com.example.SechongMaru.repository.policy.PolicyRepository;
import com.example.SechongMaru.repository.policy.SavedPolicyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PolicyDetailService {

    private final PolicyRepository policyRepo;
    private final SavedPolicyRepository savedRepo;

    /**
     * @param policyId 정책 ID
     * @param userIdOpt 로그인 사용자 Long ID (없으면 비회원)
     */
    public PolicyDetailResponseDto getPolicyDetail(Long policyId, Optional<Long> userIdOpt) {
        Policy policy = policyRepo.findById(policyId)
                .orElseThrow(() -> new IllegalArgumentException("정책을 찾을 수 없습니다. id=" + policyId));

        boolean scraped = false;
        if (userIdOpt.isPresent() && policy.getId() != null) {
            scraped = savedRepo.existsByUser_IdAndPolicy_Id(userIdOpt.get(), policy.getId());
        }

        return PolicyDetailResponseDto.builder()
                .policyId(policy.getId())
                .title(policy.getTitle())
                .applyStart(policy.getApplyStart())
                .applyEnd(policy.getApplyEnd())
                .minAge(toInteger(policy.getMinAge()))          // 엔티티가 Integer면 그대로, String이면 안전 변환
                .maxAge(toInteger(policy.getMaxAge()))
                .employStatus(policy.getEmployStatus())
                .contactInfo(policy.getContactInfo())
                .money(toInteger(policy.getMoney()))
                .duration(toInteger(policy.getDuration()))
                .requiredDocs(extractDocs(policy))
                .applyStatus(mapApplyStatus(policy.getApplyStatus()))
                .applyUrl(policy.getApplyUrl())
                .isScraped(scraped)
                .build();
    }

    // ---------- helpers ----------

    /**
     * 엔티티가 Integer 또는 String일 수 있으므로 안전하게 Integer로 변환
     */
    private Integer toInteger(Object value) {
        if (value == null) return null;
        if (value instanceof Integer i) return i;
        if (value instanceof Number n) return n.intValue();
        String s = value.toString().trim();
        if (s.isEmpty()) return null;
        try {
            return Integer.valueOf(s);
        } catch (NumberFormatException e) {
            return null; // 필요 시 로깅
        }
    }

    /**
     * applyStatus: "온라인" 포함 → 1, 그 외/미지정 → 0
     * 엔티티가 이미 숫자라면 그대로 매핑
     */
    private Integer mapApplyStatus(Object status) {
        if (status == null) return 0;
        if (status instanceof Number n) return n.intValue();
        String s = status.toString();
        return s.contains("온라인") ? 1 : 0;
    }

    /**
     * requiredDocs 추출
     * - 엔티티의 text 필드가 콤마/개행으로 구분돼 있으면 파싱
     * - 없으면 빈 리스트
     * (추후 policy_required_docs 테이블을 쓰면 거기서 조회하도록 교체)
     */
    private List<String> extractDocs(Policy policy) {
        String raw = policy.getRequiredDocs();
        if (raw == null || raw.isBlank()) return List.of();
        // 콤마 또는 줄바꿈 기준 분리
        return Arrays.stream(raw.split("[,\n]"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}
