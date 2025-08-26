package com.example.SechongMaru.repository.policy;

import com.example.SechongMaru.entity.policy.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface PolicyRepository extends JpaRepository<Policy, UUID> {
    Optional<Policy> findByTitleAndApplyStartAndApplyUrl(String title, LocalDate applyStart, String applyUrl);
}
