// src/main/java/com/example/SechongMaru/policy/controller/PolicyDebugController.java
package com.example.SechongMaru.policy.controller;

import com.example.SechongMaru.entity.policy.Policy;
import com.example.SechongMaru.entity.policy.PolicyEligibilityRule;
import com.example.SechongMaru.entity.user.User;
import com.example.SechongMaru.globals.enums.EligibilityAttribute;
import com.example.SechongMaru.policy.service.PolicyEligibilityService;
import com.example.SechongMaru.repository.policy.PolicyEligibilityRuleRepository;
import com.example.SechongMaru.repository.policy.PolicyRepository;
import com.example.SechongMaru.repository.user.UserRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/policy/debug")
public class PolicyDebugController {

    private final PolicyRepository policyRepo;
    private final PolicyEligibilityRuleRepository ruleRepo;
    private final UserRepository userRepo;
    private final PolicyEligibilityService eligibilityService;

    public PolicyDebugController(PolicyRepository policyRepo,
                                 PolicyEligibilityRuleRepository ruleRepo,
                                 UserRepository userRepo,
                                 PolicyEligibilityService eligibilityService) {
        this.policyRepo = policyRepo;
        this.ruleRepo = ruleRepo;
        this.userRepo = userRepo;
        this.eligibilityService = eligibilityService;
    }

    @GetMapping("/counts")
    public Map<String, Object> counts() {
        return Map.of("rules", ruleRepo.count(), "policies", policyRepo.count(), "users", userRepo.count());
    }

    @GetMapping("/probe")
    public List<Map<String, Object>> probe(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate base = date != null ? date : LocalDate.now(ZoneId.of("Asia/Seoul"));
        var auth = SecurityContextHolder.getContext().getAuthentication();

        User user = null;
        if (auth != null && auth.getName() != null) {
            try {
                user = userRepo.findById(Long.valueOf(auth.getName())).orElse(null);
            } catch (Exception ignored) {}
        }
        if (user == null) return List.of(Map.of("error", "no-user"));

        List<Policy> policies = policyRepo.findAll();
        List<Map<String, Object>> out = new ArrayList<>();

        for (Policy p : policies) {
            var rules = ruleRepo.findByPolicy_Id(p.getId());

            boolean eligible = true;
            List<Map<String, Object>> ruleResults = new ArrayList<>();

            for (PolicyEligibilityRule r : rules) {
                boolean pass = testRule(user, r, base);
                ruleResults.add(Map.of(
                        "attribute", r.getAttribute().name(),
                        "operator", r.getOperator().name(),
                        "valueText", r.getValueText(),
                        "min", r.getMinValue(),
                        "max", r.getMaxValue(),
                        "refInterest", r.getRefInterest() != null ? r.getRefInterest().getName() : null,
                        "pass", pass
                ));
                if (r.getAttribute() != EligibilityAttribute.interest && !pass) {
                    eligible = false;
                }
            }

            out.add(Map.of(
                    "policyId", p.getId(),
                    "title", p.getTitle(),
                    "ruleCount", rules.size(),
                    "eligible", eligible,
                    "rules", ruleResults
            ));
        }
        return out;
    }


    // 개별 룰만 평가(eligibilityService의 로직과 동일하도록 유지)
    private boolean testRule(User user, PolicyEligibilityRule rule, LocalDate base) {
        // interest는 추천용이라 자격판정에서는 true 취급
        if (rule.getAttribute() == EligibilityAttribute.interest) return true;
        try {
            // eligibilityService.isEligible 전체 대신, 한 개 룰만 검사하려면
            // 서비스에 공개 메서드가 없다면 임시로 여기서 같은 비교 로직을 구현하거나,
            // 서비스 메서드를 package-private로 빼서 호출해도 됩니다.
            // 간단히: 임시로 전체 평가 후 결과만 꺼내도 OK
            return true; // <- 필요 시 세밀 로직 복붙 가능
        } catch (Exception e) {
            return false;
        }
    }
}
