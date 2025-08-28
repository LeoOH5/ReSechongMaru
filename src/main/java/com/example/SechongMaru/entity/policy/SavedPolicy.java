package com.example.SechongMaru.entity.policy;

import com.example.SechongMaru.entity.common.BaseTimeEntity;
import com.example.SechongMaru.entity.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "saved_policies",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "policy_id"}))
public class SavedPolicy extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false) @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false) @JoinColumn(name = "policy_id")
    private Policy policy;

    @Column(nullable = false)
    @Builder.Default
    private String status = "saved"; // saved | applied

    private OffsetDateTime savedAt;

}