package com.example.SechongMaru.repository.interest;

import com.example.SechongMaru.entity.interest.Interest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InterestRepository extends JpaRepository<Interest, Long> {
    Optional<Interest> findByName(String name);
}
