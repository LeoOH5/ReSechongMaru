package com.example.SechongMaru.entity.policy;

import com.example.SechongMaru.entity.interest.Interest;
import com.example.SechongMaru.globals.enums.EligibilityAttribute;
import com.example.SechongMaru.globals.enums.EligibilityOperator;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "policy_eligibility_rules")
public class PolicyEligibilityRule {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "policy_id")
    private Policy policy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EligibilityAttribute attribute;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EligibilityOperator operator;

    @Column(length = 500)
    private String valueText;     // cityName, enum 나열 등

    private BigDecimal minValue;  // age/monthIncome lower
    private BigDecimal maxValue;  // age/monthIncome upper

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ref_interest_id")
    private Interest refInterest; // interest 룰일 때만 사용
}
