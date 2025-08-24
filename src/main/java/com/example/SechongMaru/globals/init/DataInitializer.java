
package com.example.SechongMaru.globals.init;

import com.example.SechongMaru.entity.policy.Policy;
import com.example.SechongMaru.entity.policy.SavedPolicy;
import com.example.SechongMaru.entity.user.User;
import com.example.SechongMaru.globals.enums.EmploymentStatus;
import com.example.SechongMaru.globals.enums.HouseholdType;
import com.example.SechongMaru.globals.enums.IncomeType;
import com.example.SechongMaru.mainPage.repository.MainPageSavedPolicyRepository;
import com.example.SechongMaru.repository.PolicyRepository;
import com.example.SechongMaru.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner seedMainPage(UserRepository userRepo,
                                          PolicyRepository policyRepo,
                                          MainPageSavedPolicyRepository savedRepo) {
        return args -> {
            // 1) 테스트 사용자 생성
            User u = User.builder()
                    .name("김재환")
                    .cityName("세종시")
                    .birthDate(LocalDate.of(1998, 5, 10))
                    .employStatus(EmploymentStatus.jobSeeker)
                    .incomeType(IncomeType.earned)
                    .monthIncomeMin(new BigDecimal("1800000"))
                    .monthIncomeMax(new BigDecimal("2500000"))
                    .householdType(HouseholdType.single)
                    .connectedAt(OffsetDateTime.now())
                    .build();
            u = userRepo.save(u);

            // 2) 정책 2건 (8월/8월~9월 걸치는 것)
            Policy p1 = Policy.builder()
                    .cityName("세종시")
                    .title("구직활동비 지원")
                    .employStatus("구직중")
                    .minAge(19).maxAge(34)
                    .applyStart(LocalDate.of(2025, 8, 6))
                    .applyEnd(LocalDate.of(2025, 8, 31))
                    .money("최대 20만원")
                    .duration(1)
                    .exclusiveGroup("G-EMP-1")
                    .build();

            Policy p2 = Policy.builder()
                    .cityName("세종시")
                    .title("청년월세 특별지원")
                    .employStatus("무관")
                    .minAge(19).maxAge(34)
                    .applyStart(LocalDate.of(2025, 8, 19))  // 8월에 시작 → 8월 달력에도 걸치도록
                    .applyEnd(LocalDate.of(2025, 9, 20))
                    .money("월 20만원")
                    .duration(6)
                    .exclusiveGroup("G-HOUSE-1")
                    .build();

            p1 = policyRepo.save(p1);
            p2 = policyRepo.save(p2);

            // 3) 즐겨찾기(스크랩)로 연결
            SavedPolicy sp1 = SavedPolicy.builder()
                    .user(u).policy(p1)
                    .status("saved")
                    .savedAt(OffsetDateTime.now())
                    .build();
            SavedPolicy sp2 = SavedPolicy.builder()
                    .user(u).policy(p2)
                    .status("saved")
                    .savedAt(OffsetDateTime.now())
                    .build();

            savedRepo.save(sp1);
            savedRepo.save(sp2);

            // 4) 콘솔 안내
            System.out.println("=== [SEED READY] ===");
            System.out.println("Test User ID (use as Bearer <UUID>): " + u.getId());
            System.out.println("Policy1: " + p1.getTitle() + " (" + p1.getApplyStart() + " ~ " + p1.getApplyEnd() + ")");
            System.out.println("Policy2: " + p2.getTitle() + " (" + p2.getApplyStart() + " ~ " + p2.getApplyEnd() + ")");
            System.out.println("====================");
        };
    }
}
