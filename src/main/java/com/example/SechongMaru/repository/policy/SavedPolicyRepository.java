package com.example.SechongMaru.repository.policy;

import com.example.SechongMaru.entity.policy.SavedPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


public interface SavedPolicyRepository extends JpaRepository<SavedPolicy, Long> {

    Page<SavedPolicy> findByUser_Id(UUID userId, Pageable pageable);

    boolean existsByUser_IdAndPolicy_Id(UUID userId, Long policyId);

    void deleteByUser_IdAndPolicy_Id(UUID userId, Long policyId);

    @Query("""
           select sp
             from SavedPolicy sp
             join fetch sp.policy p
            where sp.user.id = :userId
              and (p.applyStart is null or p.applyStart <= :monthEnd)
              and (p.applyEnd   is null or p.applyEnd   >= :monthStart)
            order by p.applyEnd asc nulls last
           """)
    List<SavedPolicy> findMonthlySavedPolicies(UUID userId, LocalDate monthStart, LocalDate monthEnd);
}
