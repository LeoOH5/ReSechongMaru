package com.example.SechongMaru.repository.policy;

import com.example.SechongMaru.entity.policy.SavedPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SavedPolicyRepository extends JpaRepository<SavedPolicy, Long> {

    Page<SavedPolicy> findByUser_Id(UUID userId, Pageable pageable);

    boolean existsByUser_IdAndPolicy_Id(UUID userId, Long policyId);

    // 삭제 기능 필요 시 사용
    // void deleteByUser_IdAndPolicy_Id(UUID userId, Long policyId);
}
