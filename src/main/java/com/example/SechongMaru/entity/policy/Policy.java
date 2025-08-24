package com.example.SechongMaru.entity.policy;

import com.example.SechongMaru.entity.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "policy",
        indexes = {
                @Index(name = "idx_policy_city", columnList = "cityName"),
                @Index(name = "idx_policy_apply_end", columnList = "applyEnd")
        })
public class Policy extends BaseTimeEntity {

    @Id @GeneratedValue @UuidGenerator
    private UUID id;

    private String cityName;

    @Column(nullable = false)
    private String title;

    @Lob
    private String employStatus; // 크롤링 원문 텍스트

    private Integer minAge;
    private Integer maxAge;

    @Lob
    private String selectionCriteria;

    @Lob
    private String requiredDocs;

    @Lob
    private String contactInfo;

    private String applyStatus;

    @Lob
    private String applyUrl;

    private LocalDate applyStart;
    private LocalDate applyEnd;

    @Lob
    private String money;

    private Integer duration; // 개월 수

    private String exclusiveGroup;

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PolicyEligibilityRule> eligibilityRules = new ArrayList<>();

    @OneToMany(mappedBy = "policy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PolicyRequiredDoc> requiredDocLinks = new ArrayList<>();
}