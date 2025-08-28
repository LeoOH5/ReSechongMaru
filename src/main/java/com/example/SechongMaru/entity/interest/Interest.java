package com.example.SechongMaru.entity.interest;

import com.example.SechongMaru.entity.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "interests", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Interest extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // 예: 주거, 일자리, 복지
}