package com.example.SechongMaru.entity.user;

import com.example.SechongMaru.entity.common.BaseTimeEntity;
import com.example.SechongMaru.globals.enums.EmploymentStatus;
import com.example.SechongMaru.globals.enums.HouseholdType;
import com.example.SechongMaru.globals.enums.IncomeType;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity @Table(name = "users")
public class User extends BaseTimeEntity {

    @Id @GeneratedValue @UuidGenerator
    private UUID id;

    private String name;

    private LocalDate birthDate;

    private String cityName;

    @Enumerated(EnumType.STRING)
    private EmploymentStatus employStatus;

    @Enumerated(EnumType.STRING)
    private IncomeType incomeType;

    @Min(0)
    @Column(precision = 15, scale = 2)
    private BigDecimal monthIncomeMin;

    @Column(precision = 15, scale = 2)
    private BigDecimal monthIncomeMax;

    @Enumerated(EnumType.STRING)
    private HouseholdType householdType;

    private OffsetDateTime connectedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserInterest> interests = new ArrayList<>();
}