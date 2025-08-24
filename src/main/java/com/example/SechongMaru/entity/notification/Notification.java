package com.example.SechongMaru.entity.notification;

import com.example.SechongMaru.entity.common.BaseTimeEntity;
import com.example.SechongMaru.entity.policy.Policy;
import com.example.SechongMaru.entity.user.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "notifications", indexes = {
        @Index(name = "idx_notif_user_time", columnList = "user_id, scheduled_for")
})
public class Notification extends BaseTimeEntity {

    @Id @GeneratedValue @UuidGenerator
    private UUID id;

    @ManyToOne(optional = false) @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne @JoinColumn(name = "policy_id")
    private Policy policy; // nullable

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;

    private OffsetDateTime scheduled_for;

    private OffsetDateTime sentAt;

    private boolean isRead;
}