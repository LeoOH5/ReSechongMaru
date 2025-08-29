// src/main/java/com/example/SechongMaru/my/MyService.java
package com.example.SechongMaru.my;

import com.example.SechongMaru.dto.user.InterestsUpdateRequestDto;
import com.example.SechongMaru.dto.user.UserCreateRequestDto;
import com.example.SechongMaru.dto.user.UserResponseDto;
import com.example.SechongMaru.dto.user.UserUpdateProfileRequestDto;
import com.example.SechongMaru.entity.interest.Interest;
import com.example.SechongMaru.entity.user.User;
import com.example.SechongMaru.entity.user.UserInterest;
import com.example.SechongMaru.repository.interest.InterestRepository;
import com.example.SechongMaru.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MyService {

    private final UserRepository userRepository;
    private final InterestRepository interestRepository;

    /** 마이페이지 조회 */
    @Transactional(readOnly = true)
    public UserResponseDto getMy(Long userId) {
        User u = userRepository.findWithInterestsById(userId)
                .orElseGet(() -> userRepository.findById(userId)
                        .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId)));
        return toDto(u);
    }

    /**
     * 마이페이지 정보수정(프로필 + 관심사)
     * - 프로필을 UserCreateRequestDto로 변환해 upsert 경로로 위임
     * - 이름/생일은 여기서 변경하지 않음(이름/생일 변경은 /api/myinfo)
     */
    @Transactional
    public UserResponseDto updateMy(Long userId,
                                    UserUpdateProfileRequestDto profile,
                                    InterestsUpdateRequestDto interestsReq) {
        UserCreateRequestDto userReq = null;
        if (profile != null) {
            userReq = new UserCreateRequestDto(
                    null,                      // name: 수정 API에서는 변경하지 않음
                    null,                      // birthDate: 수정 API에서는 변경하지 않음
                    profile.cityName(),
                    profile.employStatus(),
                    profile.incomeType(),
                    profile.monthIncomeMin(),
                    profile.monthIncomeMax(),
                    profile.householdType()
            );
        }
        return upsertMyInfo(userId, userReq, interestsReq);
    }

    /** 기본정보 입력(이름/생일 포함) + 관심사 */
    @Transactional
    public UserResponseDto upsertMyInfo(Long userId,
                                        UserCreateRequestDto userReq,
                                        InterestsUpdateRequestDto interestsReq) {
        User u = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: " + userId));

        // 1) 기본정보 반영
        if (userReq != null) {
            if (userReq.name() != null && !userReq.name().trim().isEmpty()) {
                u.setName(userReq.name().trim());
            }
            if (userReq.birthDate() != null) u.setBirthDate(userReq.birthDate());
            u.setCityName(userReq.cityName());
            u.setEmployStatus(userReq.employStatus());
            u.setIncomeType(userReq.incomeType());
            u.setMonthIncomeMin(userReq.monthIncomeMin());
            u.setMonthIncomeMax(userReq.monthIncomeMax());
            u.setHouseholdType(userReq.householdType());

            if (u.getMonthIncomeMin() != null && u.getMonthIncomeMax() != null
                    && u.getMonthIncomeMax().compareTo(u.getMonthIncomeMin()) < 0) {
                throw new IllegalArgumentException("monthIncomeMax must be >= monthIncomeMin");
            }
        }

        if (u.getConnectedAt() == null) u.setConnectedAt(OffsetDateTime.now());

        // 2) 관심사 업데이트 (diff 방식)
        upsertInterestsByDiff(u, interestsReq);

        userRepository.save(u); // flush

        // 3) 재조회(EntityGraph) 후 DTO로 변환 (프록시/N+1 이슈 회피)
        User reloaded = userRepository.findWithInterestsById(userId).orElse(u);
        return toDto(reloaded);
    }

    /**
     * 관심사 diff 업데이트:
     *  - 기존과 동일한 (user, interest) 조합은 그대로 둠 → 중복 INSERT 방지(유니크 충돌 없음)
     *  - 없는 것만 추가, 빠진 것만 제거
     *  - orphanRemoval=true, cascade=ALL 전제
     */
    private void upsertInterestsByDiff(User u, InterestsUpdateRequestDto req) {
        if (req == null) return;

        // 목표 집합(target) 만들기: id + name(upsert)
        Set<UUID> targetIds = new LinkedHashSet<>();
        if (req.interestIds() != null) {
            for (UUID id : req.interestIds()) if (id != null) targetIds.add(id);
        }

        if (req.interestNames() != null) {
            for (String nm : req.interestNames()) {
                if (nm == null || nm.isBlank()) continue;
                String name = nm.trim();
                Interest it = interestRepository.findByName(name)
                        .orElseGet(() -> interestRepository.save(Interest.builder().name(name).build()));
                targetIds.add(it.getId());
            }
        }

        // 컬렉션 준비
        if (u.getInterests() == null) {
            u.setInterests(new ArrayList<>());
        }

        // 현재 보유 집합(current)
        Map<UUID, UserInterest> currentById = new LinkedHashMap<>();
        for (UserInterest ui : new ArrayList<>(u.getInterests())) { // 복사본 순회
            if (ui.getInterest() != null && ui.getInterest().getId() != null) {
                currentById.put(ui.getInterest().getId(), ui);
            }
        }

        // 1) 제거: target에 없는 것들은 제거
        for (UserInterest ui : new ArrayList<>(u.getInterests())) {
            UUID iid = (ui.getInterest() != null) ? ui.getInterest().getId() : null;
            if (iid == null || !targetIds.contains(iid)) {
                u.getInterests().remove(ui); // orphanRemoval로 DB 삭제
            }
        }

        // 2) 추가: 현재 없고 target에만 있는 것들 추가
        if (!targetIds.isEmpty()) {
            List<Interest> targets = interestRepository.findAllById(targetIds);
            Set<UUID> existing = currentById.keySet(); // 이미 있는 interest id
            for (Interest it : targets) {
                if (it == null || it.getId() == null) continue;
                if (!existing.contains(it.getId())) {
                    u.getInterests().add(
                            UserInterest.builder()
                                    .user(u)
                                    .interest(it)
                                    .createdIt(OffsetDateTime.now())
                                    .build()
                    );
                }
            }
        }
        // targetIds가 빈 경우(= 모두 비우기)면 위의 제거 루프만 실행되어 컬렉션 clean 상태가 됨.
    }

    /** 엔티티 → 응답 DTO (LocalDateTime → OffsetDateTime 변환 + null 방어) */
    private UserResponseDto toDto(User u) {
        List<String> interestNames = (u.getInterests() == null)
                ? List.of()
                : u.getInterests().stream()
                .map(ui -> ui != null && ui.getInterest() != null ? ui.getInterest().getName() : null)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        var createdAt = (u.getCreatedAt() == null) ? null : u.getCreatedAt().atOffset(ZoneOffset.UTC);
        var updatedAt = (u.getUpdatedAt() == null) ? null : u.getUpdatedAt().atOffset(ZoneOffset.UTC);

        return new UserResponseDto(
                u.getId(),
                u.getName(),
                u.getBirthDate(),
                u.getCityName(),
                u.getEmployStatus(),
                u.getIncomeType(),
                u.getMonthIncomeMin(),
                u.getMonthIncomeMax(),
                u.getHouseholdType(),
                u.getConnectedAt(),   // OffsetDateTime
                interestNames,
                createdAt,
                updatedAt
        );
    }
}
