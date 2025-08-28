package com.example.SechongMaru.entity.policy;

import com.example.SechongMaru.entity.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "policy_required_docs",
        uniqueConstraints = @UniqueConstraint(columnNames = {"policy_id", "docKey"}))
public class PolicyRequiredDoc extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "policy_id")
    private Policy policy;

    @Column(nullable = false)
    private String docKey; // 문서명

    private String docUrl; // 링크(없으면 null)
}