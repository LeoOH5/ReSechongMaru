package com.example.SechongMaru.repository.user;

import com.example.SechongMaru.entity.user.UserInterest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserInterestRepository extends JpaRepository<UserInterest, UUID> { }
