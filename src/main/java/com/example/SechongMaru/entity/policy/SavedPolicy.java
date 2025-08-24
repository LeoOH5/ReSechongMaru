package com.example.SechongMaru.entity.policy;

import com.example.SechongMaru.entity.common.BaseTimeEntity;
import com.example.SechongMaru.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "saved_policies",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "policy_id"}))
public class SavedPolicy extends BaseTimeEntity {

    @Id @GeneratedValue @UuidGenerator
    private UUID id;

    @ManyToOne(optional = false) @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false) @JoinColumn(name = "policy_id")
    private Policy policy;

    @Builder.Default
    @Column(nullable = false)
    private String status = "saved"; // saved | applied

    private OffsetDateTime savedAt;

}