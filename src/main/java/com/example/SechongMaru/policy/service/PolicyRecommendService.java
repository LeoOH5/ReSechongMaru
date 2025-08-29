// src/main/java/com/example/SechongMaru/policy/service/PolicyRecommendService.java
package com.example.SechongMaru.policy.service;

import com.example.SechongMaru.entity.policy.Policy;
import com.example.SechongMaru.entity.user.User;
import com.example.SechongMaru.policy.dto.RecommendPoliciesResponseDto;
import com.example.SechongMaru.policy.dto.RecommendPolicyItemDto;
import com.example.SechongMaru.repository.policy.PolicyRepository;
import com.example.SechongMaru.repository.policy.SavedPolicyRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PolicyRecommendService {

    private final PolicyRepository policyRepo;
    private final SavedPolicyRepository savedRepo;
    private final PolicyEligibilityService eligibilityService;

    public PolicyRecommendService(
            PolicyRepository policyRepo,
            SavedPolicyRepository savedRepo,
            PolicyEligibilityService eligibilityService) {
        this.policyRepo = policyRepo;
        this.savedRepo = savedRepo;
        this.eligibilityService = eligibilityService;
    }

    public RecommendPoliciesResponseDto recommend(User user, int page0, int size, LocalDate base) {

        // 1) 정책 전부 가져와서 적합한 것만 필터 (필요 시 JPQL로 바꿔도 됨)
        List<Policy> all = policyRepo.findAll();

        List<Policy> eligible = all.stream()
                .filter(p -> eligibilityService.isEligible(user, p, base))
                .toList();

        int total = eligible.size();

        // 2) 정렬/페이징 (마감일 오름차순; null은 뒤로)
        var sorted = eligible.stream()
                .sorted((a, b) -> {
                    LocalDate ea = a.getApplyEnd();
                    LocalDate eb = b.getApplyEnd();
                    if (ea == null && eb == null) return 0;
                    if (ea == null) return 1;
                    if (eb == null) return -1;
                    return ea.compareTo(eb);
                })
                .toList();

        int from = Math.min(page0 * size, sorted.size());
        int to = Math.min(from + size, sorted.size());
        List<Policy> pageItems = sorted.subList(from, to);

        // 3) DTO 매핑 (NULL-세이프)
        List<RecommendPolicyItemDto> items = pageItems.stream().map(p -> {
            boolean isScraped = false;
            if (user.getId() != null && p.getId() != null) {
                isScraped = savedRepo.existsByUser_IdAndPolicy_Id(user.getId(), p.getId());
            }
            return RecommendPolicyItemDto.builder()
                    .policyId(p.getId())
                    .title(Optional.ofNullable(p.getTitle()).orElse(""))
                    .applyStart(Optional.ofNullable(p.getApplyStart()).map(LocalDate::toString).orElse(null))
                    .applyEnd(Optional.ofNullable(p.getApplyEnd()).map(LocalDate::toString).orElse(null))
                    .isScraped(isScraped)
                    .recommend(true)
                    .build();
        }).toList();

        boolean hasNext = (to < sorted.size());

        return RecommendPoliciesResponseDto.builder()
                .paging(RecommendPoliciesResponseDto.Paging.builder()
                        .page(page0 + 1)
                        .size(size)
                        .total(total)
                        .has_next(hasNext)
                        .build())
                .items(items)
                .build();
    }
}
