package com.example.SechongMaru.policy.controller;

import com.example.SechongMaru.entity.policy.Policy;
import com.example.SechongMaru.entity.user.User;
import com.example.SechongMaru.policy.service.PolicyEligibilityService;
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
@RequestMapping("/api/policies")
public class PolicyEligibilityController {

    private final PolicyRepository policyRepo;
    private final UserRepository userRepo;
    private final PolicyEligibilityService eligibilityService;

    public PolicyEligibilityController(PolicyRepository policyRepo, UserRepository userRepo,
                                       PolicyEligibilityService eligibilityService) {
        this.policyRepo = policyRepo;
        this.userRepo = userRepo;
        this.eligibilityService = eligibilityService;
    }

    @GetMapping("/eligible")
    public List<Map<String, Object>> getEligiblePolicies(
            @RequestParam(value = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        LocalDate baseDate = (date != null) ? date : LocalDate.now(ZoneId.of("Asia/Seoul"));

        Optional<UUID> userId = currentUserId();
        if (userId.isEmpty()) return List.of();

        User user = userRepo.findById(userId.get()).orElse(null);
        if (user == null) return List.of();

        List<Policy> all = policyRepo.findAll();
        var eligibles = eligibilityService.filterEligible(user, all, baseDate);

        return eligibles.stream().map(p -> Map.<String, Object>of(
                "policyId", p.getId(),               // ★ Long
                "title", p.getTitle(),
                "cityName", p.getCityName(),
                "applyStart", p.getApplyStart(),
                "applyEnd", p.getApplyEnd(),
                "interestMatched", eligibilityService.interestMatched(user, p)
        )).collect(Collectors.toList());
    }

    private Optional<UUID> currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) return Optional.empty();
        try { return Optional.of(UUID.fromString(auth.getName())); }
        catch (IllegalArgumentException e) { return Optional.empty(); }
    }
}
