package com.example.SechongMaru.repository.policy;

import com.example.SechongMaru.entity.policy.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PolicyRepository extends JpaRepository<Policy, UUID> {

    // 기존: 중복 방지/특정 레코드 식별에 사용
    Optional<Policy> findByTitleAndApplyStartAndApplyUrl(String title, LocalDate applyStart, String applyUrl);

    // 추가: 제목에 q가 포함된 정책 목록(대소문자 무시)
    List<Policy> findByTitleContainingIgnoreCase(String q);
}
