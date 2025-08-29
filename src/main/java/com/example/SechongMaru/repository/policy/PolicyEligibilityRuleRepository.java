package com.example.SechongMaru.repository.policy;

import com.example.SechongMaru.entity.policy.PolicyEligibilityRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolicyEligibilityRuleRepository extends JpaRepository<PolicyEligibilityRule, Long> {
    List<PolicyEligibilityRule> findByPolicy_Id(Long policyId);
}
