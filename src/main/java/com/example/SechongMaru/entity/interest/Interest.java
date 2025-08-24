package com.example.SechongMaru.entity.interest;

import com.example.SechongMaru.entity.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "interests", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Interest extends BaseTimeEntity {

    @Id @GeneratedValue @UuidGenerator
    private UUID id;

    @Column(nullable = false)
    private String name; // 예: 주거, 일자리, 복지
}