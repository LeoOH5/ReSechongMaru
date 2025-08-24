//UserInterest (M:N 조인)
package com.example.SechongMaru.entity.user;

import com.example.SechongMaru.entity.interest.Interest;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
@Entity
@Table(name = "user_interests",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "interest_id"}))
public class UserInterest {

    @Id @GeneratedValue @UuidGenerator
    private UUID id;

    @ManyToOne(optional = false) @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(optional = false) @JoinColumn(name = "interest_id")
    private Interest interest;

    private OffsetDateTime createdIt;
}