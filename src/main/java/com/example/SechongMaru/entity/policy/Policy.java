
package com.example.SechongMaru.entity.policy;

import com.example.SechongMaru.entity.common.BaseTimeEntity; // 있으면 사용, 없으면 @PrePersist로 직접 관리
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "policies")
public class Policy extends BaseTimeEntity { // BaseTimeEntity 없으면 제거해도 됨

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String cityName;
    private String employStatus;
    private Integer minAge;
    private Integer maxAge;

    @Column(columnDefinition = "text")
    private String selectionCriteria;

    @Column(columnDefinition = "text")
    private String requiredDocs;

    @Column(columnDefinition = "text")
    private String contactInfo;

    @Column(columnDefinition = "text")
    private String applyStatus;

    @Column(columnDefinition = "text")
    private String applyUrl;

    private LocalDate applyStart;
    private LocalDate applyEnd;

    @Column(columnDefinition = "text")
    private String money;

    private Integer duration;          // months
    private String exclusiveGroup;     // mutually exclusive group code
}
