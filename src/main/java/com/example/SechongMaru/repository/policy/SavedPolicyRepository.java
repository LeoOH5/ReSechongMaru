package com.example.SechongMaru.repository.policy;

import com.example.SechongMaru.entity.policy.SavedPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SavedPolicyRepository extends JpaRepository<SavedPolicy, UUID> {
    boolean existsByUser_IdAndPolicy_Id(UUID userId, Long policyId);
}
