// 임시 데이터용
package com.example.SechongMaru.repository;

import com.example.SechongMaru.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {}
