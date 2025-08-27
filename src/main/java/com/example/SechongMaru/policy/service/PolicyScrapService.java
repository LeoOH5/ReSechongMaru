package com.example.SechongMaru.policy.service;

import com.example.SechongMaru.entity.policy.Policy;
import com.example.SechongMaru.entity.policy.SavedPolicy;
import com.example.SechongMaru.entity.user.User;
import com.example.SechongMaru.policy.dto.ScrapItemDto;
import com.example.SechongMaru.policy.dto.ScrapListResponseDto;
import com.example.SechongMaru.repository.policy.PolicyRepository;
import com.example.SechongMaru.repository.policy.SavedPolicyRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PolicyScrapService {

    private final SavedPolicyRepository savedRepo;
    private final PolicyRepository policyRepo;
    private final EntityManager em;

    /** 스크랩 목록 조회 */
    public ScrapListResponseDto getMyScraps(UUID userId, int page, int size) {
        Pageable pageable = PageRequest.of(Math.max(0, page - 1), size, Sort.by(Sort.Direction.DESC, "savedAt"));
        Page<SavedPolicy> result = savedRepo.findByUser_Id(userId, pageable);

        List<ScrapItemDto> items = result.getContent().stream()
                .map(sp -> {
                    Policy p = sp.getPolicy();
                    return ScrapItemDto.builder()
                            .policyId(p.getId())
                            .title(p.getTitle())
                            .applyStart(p.getApplyStart())
                            .applyEnd(p.getApplyEnd())
                            .build();
                })
                .toList();

        Map<String, Object> paging = Map.of(
                "page", page,
                "size", size,
                "total", result.getTotalElements(),
                "has_next", result.hasNext()
        );

        return ScrapListResponseDto.builder()
                .paging(paging)
                .items(items)
                .build();
    }

    /** 스크랩 저장 */
    @Transactional
    public void addScrap(UUID userId, Long policyId) {
        if (savedRepo.existsByUser_IdAndPolicy_Id(userId, policyId)) {
            // 이미 스크랩되어 있으면 그냥 종료(또는 IllegalStateException 던지는 방식도 가능)
            return;
        }

        Policy policy = policyRepo.findById(policyId)
                .orElseThrow(() -> new IllegalArgumentException("정책을 찾을 수 없습니다. id=" + policyId));

        // 실제 조회 없이 키만 가진 프록시로 연관관계 연결
        User userRef = em.getReference(User.class, userId);

        SavedPolicy saved = SavedPolicy.builder()
                .user(userRef)
                .policy(policy)
                .status("saved")            // 엔티티 기본값 정책에 맞춰 조정 가능
                .savedAt(OffsetDateTime.now())
                .build();

        savedRepo.save(saved);
    }

    @Transactional
    public void deleteScrap(UUID userId, Long policyId) {
        if (!savedRepo.existsByUser_IdAndPolicy_Id(userId, policyId)) {
            return; // 이미 없으면 무시 (원하면 예외 던져도 됨)
        }
        savedRepo.deleteByUser_IdAndPolicy_Id(userId, policyId);
    }
}
