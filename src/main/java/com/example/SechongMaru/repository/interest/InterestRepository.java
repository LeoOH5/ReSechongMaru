package com.example.SechongMaru.repository.interest;

import com.example.SechongMaru.entity.interest.Interest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InterestRepository extends JpaRepository<Interest, UUID> {
    Optional<Interest> findByName(String name);
}
