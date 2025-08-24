package com.example.SechongMaru.mainPage.repository;

import com.example.SechongMaru.entity.policy.SavedPolicy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface MainPageSavedPolicyRepository extends JpaRepository<SavedPolicy, UUID> {

    @Query("""
           SELECT sp
             FROM SavedPolicy sp
             JOIN sp.policy p
            WHERE sp.user.id = :userId
              AND (p.applyStart IS NULL OR p.applyStart <= :endDate)
              AND (p.applyEnd   IS NULL OR p.applyEnd   >= :startDate)
           ORDER BY p.applyStart NULLS LAST, p.applyEnd NULLS LAST, p.title ASC
           """)
    List<SavedPolicy> findSavedEventsOverlappingMonth(UUID userId, LocalDate startDate, LocalDate endDate);
}
