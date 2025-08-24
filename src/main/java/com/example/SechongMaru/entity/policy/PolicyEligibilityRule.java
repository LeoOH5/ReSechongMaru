package com.example.SechongMaru.entity.policy;

import com.example.SechongMaru.entity.common.BaseTimeEntity;
import com.example.SechongMaru.entity.interest.Interest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "policy_eligibility_rules")
public class PolicyEligibilityRule extends BaseTimeEntity {

    @Id @GeneratedValue @UuidGenerator
    private UUID id;

    @ManyToOne(optional = false) @JoinColumn(name = "policy_id")
    private Policy policy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EligibilityAttribute attribute;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EligibilityOperator operator;

    // 문자열 비교값(ex: cityName='세종시', employStatus='student,job_seeker')
    private String valueText;

    // 숫자 범위 비교
    @Column(precision = 15, scale = 2)
    private BigDecimal minValue;

    @Column(precision = 15, scale = 2)
    private BigDecimal maxValue;

    // 관심사 비교 시
    @ManyToOne @JoinColumn(name = "ref_interest_id")
    private Interest refInterest;
}