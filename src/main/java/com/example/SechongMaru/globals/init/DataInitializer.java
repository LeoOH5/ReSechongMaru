// 더미데이터용
package com.example.SechongMaru.globals.init;

import com.example.SechongMaru.entity.interest.Interest;
import com.example.SechongMaru.entity.policy.Policy;
import com.example.SechongMaru.entity.policy.PolicyEligibilityRule;
import com.example.SechongMaru.entity.user.User;
import com.example.SechongMaru.entity.user.UserInterest;
import com.example.SechongMaru.globals.enums.*;
import com.example.SechongMaru.repository.policy.PolicyEligibilityRuleRepository;
import com.example.SechongMaru.repository.policy.PolicyRepository;
import com.example.SechongMaru.repository.interest.InterestRepository;
import com.example.SechongMaru.repository.user.UserInterestRepository;
import com.example.SechongMaru.repository.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedPolicies(UserRepository userRepo,
                                          PolicyRepository policyRepo,
                                          PolicyEligibilityRuleRepository ruleRepo,
                                          InterestRepository interestRepo,
                                          UserInterestRepository userInterestRepo) {
        return args -> {

            // 0) 관심사 사전 (주거, 일자리)
            Interest housing = interestRepo.findByName("주거")
                    .orElseGet(() -> interestRepo.save(Interest.builder().name("주거").build()));
            Interest job = interestRepo.findByName("일자리")
                    .orElseGet(() -> interestRepo.save(Interest.builder().name("일자리").build()));

            // 1) 테스트 사용자(없으면 생성)
            User u = userRepo.findAll().stream().findFirst().orElseGet(() ->
                    userRepo.save(User.builder()
                            .name("김재환")
                            .birthDate(LocalDate.of(1998, 5, 10))
                            .cityName("세종시")
                            .employStatus(EmploymentStatus.jobSeeker)
                            .incomeType(IncomeType.earned)
                            .monthIncomeMin(new BigDecimal("1800000"))
                            .monthIncomeMax(new BigDecimal("2500000"))
                            .householdType(HouseholdType.single)
                            .connectedAt(OffsetDateTime.now())
                            .build())
            );

            // 2) 사용자 관심사 연결(중복 저장 방지: unique(user, interest))
            try {
                userInterestRepo.save(UserInterest.builder().user(u).interest(housing).build());
            } catch (Exception ignore) { }
            try {
                userInterestRepo.save(UserInterest.builder().user(u).interest(job).build());
            } catch (Exception ignore) { }

            // 3) 정책 A (8월)
            Policy pA = policyRepo.save(Policy.builder()
                    .cityName("세종시")
                    .title("세종시 청년 월세지원")
                    .employStatus("무관")
                    .minAge(19).maxAge(34)
                    .applyStart(LocalDate.of(2025, 8, 1))
                    .applyEnd(LocalDate.of(2025, 8, 31))
                    .money("월 20만원")
                    .duration(6)
                    .exclusiveGroup("G-HOUSE-1")
                    .build());

            ruleRepo.save(PolicyEligibilityRule.builder()
                    .policy(pA).attribute(EligibilityAttribute.cityName).operator(EligibilityOperator.eq)
                    .valueText("세종시").build());
            ruleRepo.save(PolicyEligibilityRule.builder()
                    .policy(pA).attribute(EligibilityAttribute.age).operator(EligibilityOperator.between)
                    .minValue(BigDecimal.valueOf(19)).maxValue(BigDecimal.valueOf(34)).build());
            ruleRepo.save(PolicyEligibilityRule.builder()
                    .policy(pA).attribute(EligibilityAttribute.incomeType).operator(EligibilityOperator.in)
                    .valueText("earned,business").build());
            ruleRepo.save(PolicyEligibilityRule.builder()
                    .policy(pA).attribute(EligibilityAttribute.monthIncome).operator(EligibilityOperator.lte)
                    .maxValue(new BigDecimal("3342668")).build());
            // 관심사(자격판정에선 무시, 추천/알림용)
            ruleRepo.save(PolicyEligibilityRule.builder()
                    .policy(pA).attribute(EligibilityAttribute.interest).operator(EligibilityOperator.contains)
                    .refInterest(housing).build());

            // 4) 정책 B (9월)
            Policy pB = policyRepo.save(Policy.builder()
                    .cityName("세종시")
                    .title("청년 취업 바우처")
                    .employStatus("구직/학생")
                    .minAge(19).maxAge(34)
                    .applyStart(LocalDate.of(2025, 9, 1))
                    .applyEnd(LocalDate.of(2025, 9, 20))
                    .money("최대 30만원")
                    .duration(1)
                    .exclusiveGroup("G-EMP-1")
                    .build());

            ruleRepo.save(PolicyEligibilityRule.builder()
                    .policy(pB).attribute(EligibilityAttribute.cityName).operator(EligibilityOperator.eq)
                    .valueText("세종시").build());
            ruleRepo.save(PolicyEligibilityRule.builder()
                    .policy(pB).attribute(EligibilityAttribute.age).operator(EligibilityOperator.between)
                    .minValue(BigDecimal.valueOf(19)).maxValue(BigDecimal.valueOf(34)).build());
            ruleRepo.save(PolicyEligibilityRule.builder()
                    .policy(pB).attribute(EligibilityAttribute.employStatus).operator(EligibilityOperator.in)
                    .valueText("jobSeeker,student").build());
            ruleRepo.save(PolicyEligibilityRule.builder()
                    .policy(pB).attribute(EligibilityAttribute.interest).operator(EligibilityOperator.contains)
                    .refInterest(job).build());

            System.out.println("=== [SEED POLICY+INTEREST READY] ===");
            System.out.println("User (Bearer UUID): " + u.getId());
            System.out.println("Policy A id=" + pA.getId() + ", Policy B id=" + pB.getId());
        };
    }
}
