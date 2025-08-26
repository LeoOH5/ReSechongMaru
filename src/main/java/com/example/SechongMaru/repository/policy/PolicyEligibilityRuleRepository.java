package com.example.SechongMaru.repository.policy;

import com.example.SechongMaru.entity.policy.PolicyEligibilityRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PolicyEligibilityRuleRepository extends JpaRepository<PolicyEligibilityRule, UUID> {
    List<PolicyEligibilityRule> findByPolicy_Id(Long policyId);
}
