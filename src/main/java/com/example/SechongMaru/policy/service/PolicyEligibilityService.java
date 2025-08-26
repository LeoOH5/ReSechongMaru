package com.example.SechongMaru.policy.service;

import com.example.SechongMaru.entity.policy.Policy;
import com.example.SechongMaru.entity.policy.PolicyEligibilityRule;
import com.example.SechongMaru.entity.user.User;
import com.example.SechongMaru.globals.enums.EligibilityAttribute;
import com.example.SechongMaru.globals.enums.EligibilityOperator;
import com.example.SechongMaru.repository.policy.PolicyEligibilityRuleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PolicyEligibilityService {

    private final PolicyEligibilityRuleRepository ruleRepo;

    public PolicyEligibilityService(PolicyEligibilityRuleRepository ruleRepo) {
        this.ruleRepo = ruleRepo;
    }


    public boolean isEligible(User user, Policy policy, LocalDate baseDate) {
        // ★ changed: repo 시그니처 Long
        List<PolicyEligibilityRule> rules = ruleRepo.findByPolicy_Id(policy.getId());
        for (PolicyEligibilityRule r : rules) {
            if (!matchRule(user, r, baseDate)) return false;
        }
        return true;
    }

    public List<Policy> filterEligible(User user, Collection<Policy> policies, LocalDate baseDate) {
        return policies.stream().filter(p -> isEligible(user, p, baseDate)).collect(Collectors.toList());
    }

    public boolean interestMatched(User user, Policy policy) {
        Set<String> userInterests = Optional.ofNullable(user.getInterests())
                .orElse(List.of())
                .stream()
                .map(ui -> ui.getInterest().getName())
                .filter(Objects::nonNull)
                .map(String::trim).map(String::toLowerCase)
                .collect(Collectors.toSet());

        var rules = ruleRepo.findByPolicy_Id(policy.getId());
        var interestRules = rules.stream()
                .filter(r -> r.getAttribute() == EligibilityAttribute.interest)
                .toList();

        for (var r : interestRules) {
            if (r.getRefInterest() != null && r.getRefInterest().getName() != null) {
                if (userInterests.contains(r.getRefInterest().getName().trim().toLowerCase())) return true;
            }
            String vt = r.getValueText() == null ? "" : r.getValueText();
            Set<String> needs = Arrays.stream(vt.split(","))
                    .map(String::trim).filter(s -> !s.isBlank())
                    .map(String::toLowerCase).collect(Collectors.toSet());
            if (!needs.isEmpty() && needs.stream().anyMatch(userInterests::contains)) return true;
        }
        return false;
    }

    // --- internals ---
    private boolean matchRule(User user, PolicyEligibilityRule rule, LocalDate baseDate) {
        var attr = rule.getAttribute();
        var op = rule.getOperator();

        return switch (attr) {
            case age -> compareNumber(calcAge(user.getBirthDate(), baseDate), rule, op);
            case employStatus -> compareText(enumName(user.getEmployStatus()), rule, op);
            case incomeType -> compareText(enumName(user.getIncomeType()), rule, op);
            case monthIncome -> {
                BigDecimal income = user.getMonthIncomeMax() != null ? user.getMonthIncomeMax() : user.getMonthIncomeMin();
                if (income == null) income = BigDecimal.ZERO;
                yield compareDecimal(income, rule, op);
            }
            case cityName -> compareText(safe(user.getCityName()), rule, op);
            case householdType -> compareText(enumName(user.getHouseholdType()), rule, op);
            case interest -> true; // 자격판정에서는 무시
        };
    }

    private boolean compareNumber(Integer left, PolicyEligibilityRule r, EligibilityOperator op) {
        if (left == null) return false;
        return compareDecimal(BigDecimal.valueOf(left), r, op);
    }
    private boolean compareDecimal(BigDecimal left, PolicyEligibilityRule r, EligibilityOperator op) {
        var min = r.getMinValue(); var max = r.getMaxValue();
        return switch (op) {
            case gte -> (min != null && left.compareTo(min) >= 0);
            case lte -> (max != null && left.compareTo(max) <= 0);
            case between -> (min != null && max != null && left.compareTo(min) >= 0 && left.compareTo(max) <= 0);
            default -> false;
        };
    }
    private boolean compareText(String left, PolicyEligibilityRule r, EligibilityOperator op) {
        String L = left == null ? "" : left.trim();
        String vt = r.getValueText() == null ? "" : r.getValueText();

        return switch (op) {
            case eq -> (!L.isBlank() && L.equalsIgnoreCase(vt.trim()));
            case in, contains -> {
                Set<String> set = Arrays.stream(vt.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isBlank())
                        .map(String::toLowerCase)
                        .collect(Collectors.toSet());
                yield (!L.isBlank() && set.contains(L.toLowerCase()));
            }
            default -> false;
        };
    }

    private static boolean isBlank(String s) { return s == null || s.isBlank(); }
    private static String safe(String s) { return s == null ? "" : s; }
    private static String enumName(Enum<?> e) { return e == null ? "" : e.name(); }
    private static int calcAge(java.time.LocalDate birth, java.time.LocalDate base) {
        if (birth == null || base == null) return 0;
        return Period.between(birth, base).getYears();
    }
}
