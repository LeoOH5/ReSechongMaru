package com.example.SechongMaru.repository.user;

import com.example.SechongMaru.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {}
