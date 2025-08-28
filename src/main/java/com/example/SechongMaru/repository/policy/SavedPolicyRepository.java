package com.example.SechongMaru.repository.policy;

import com.example.SechongMaru.entity.policy.SavedPolicy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface SavedPolicyRepository extends JpaRepository<SavedPolicy, Long> {

    Page<SavedPolicy> findByUser_Id(Long userId, Pageable pageable);

    boolean existsByUser_IdAndPolicy_Id(Long userId, Long policyId);

    void deleteByUser_IdAndPolicy_Id(Long userId, Long policyId);

    @Query("""
           select sp
             from SavedPolicy sp
             join fetch sp.policy p
            where sp.user.id = :userId
              and (p.applyStart is null or p.applyStart <= :monthEnd)
              and (p.applyEnd   is null or p.applyEnd   >= :monthStart)
            order by p.applyEnd asc nulls last
           """)
    List<SavedPolicy> findMonthlySavedPolicies(Long userId, LocalDate monthStart, LocalDate monthEnd);
}
